package ar.com.GestionTurnos.config;

import ar.com.GestionTurnos.dao.IRolesDao;
import ar.com.GestionTurnos.dao.IUsuariosDao;
import ar.com.GestionTurnos.entities.Roles;
import ar.com.GestionTurnos.entities.Usuarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

/**
 * Crea los roles básicos y el usuario administrador cuando la base de datos
 * está vacía (por ejemplo, en el primer despliegue). Es idempotente: si los
 * datos ya existen no modifica nada.
 *
 * Se configura por variables de entorno:
 *   APP_BOOTSTRAP_ENABLED (por defecto true)
 *   ADMIN_USERNAME        (por defecto admin)
 *   ADMIN_PASSWORD        (si está vacío se genera una contraseña aleatoria)
 *   ADMIN_ROLE            (por defecto Administrador)
 */
@Component
public class DatosIniciales implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);
    private static final int LARGO_PASSWORD_GENERADA = 18;

    private final IRolesDao rolesDao;
    private final IUsuariosDao usuariosDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.enabled:true}")
    private boolean habilitado;

    @Value("${app.bootstrap.admin-username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    @Value("${app.bootstrap.admin-role:Administrador}")
    private String adminRole;

    public DatosIniciales(IRolesDao rolesDao, IUsuariosDao usuariosDao, PasswordEncoder passwordEncoder) {
        this.rolesDao = rolesDao;
        this.usuariosDao = usuariosDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }

        List<Roles> existentes = rolesDao.findAll();
        Roles administrador = asegurarRol("Administrador", 1, existentes);
        asegurarRol("Invitado", 2, existentes);
        asegurarRol("Usuarios", 3, existentes);
        Roles rolAdmin = buscarRol(adminRole, existentes);
        if (rolAdmin != null) {
            administrador = rolAdmin;
        }

        String usuario = adminUsername == null || adminUsername.isBlank() ? "admin" : adminUsername.trim();
        if (usuariosDao.findOneByUsername(usuario) != null) {
            log.info("El usuario '{}' ya existe; no se crean datos iniciales.", usuario);
            return;
        }

        String password = adminPassword == null ? "" : adminPassword.trim();
        boolean generada = password.isEmpty();
        if (generada) {
            password = generarPassword();
        }

        Usuarios nuevo = new Usuarios();
        nuevo.setUsername(usuario);
        nuevo.setPassword(passwordEncoder.encode(password));
        // pass_orig se deja vacío: la contraseña real queda solo como hash BCrypt.
        nuevo.setPassorig("");
        nuevo.setInactivo(false);
        nuevo.setRol(administrador);
        usuariosDao.save(nuevo);

        if (generada) {
            log.warn("======================================================================");
            log.warn("Se creó el usuario administrador '{}' con una contraseña aleatoria:", usuario);
            log.warn("  Contraseña: {}", password);
            log.warn("Cambiala al ingresar o definí el secret ADMIN_PASSWORD y recreá la base.");
            log.warn("======================================================================");
        } else {
            log.info("Se creó el usuario administrador '{}'.", usuario);
        }
    }

    private Roles asegurarRol(String descripcion, int nivel, List<Roles> existentes) {
        Roles encontrado = buscarRol(descripcion, existentes);
        if (encontrado != null) {
            return encontrado;
        }
        Roles nuevo = new Roles();
        nuevo.setDescrip(descripcion);
        nuevo.setNivel(nivel);
        Roles guardado = rolesDao.save(nuevo);
        existentes.add(guardado);
        log.info("Rol '{}' creado.", descripcion);
        return guardado;
    }

    private Roles buscarRol(String descripcion, List<Roles> roles) {
        if (descripcion == null) {
            return null;
        }
        for (Roles rol : roles) {
            if (rol.getDescrip() != null && rol.getDescrip().trim().equalsIgnoreCase(descripcion.trim())) {
                return rol;
            }
        }
        return null;
    }

    private String generarPassword() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
                .substring(0, LARGO_PASSWORD_GENERADA);
    }
}
