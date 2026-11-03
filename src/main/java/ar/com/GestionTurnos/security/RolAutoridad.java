package ar.com.GestionTurnos.security;

import ar.com.GestionTurnos.entities.Roles;

import java.text.Normalizer;
import java.util.Locale;

public final class RolAutoridad {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    private RolAutoridad() {
    }

    public static String de(Roles rol) {
        if (rol == null || rol.getDescrip() == null) {
            return USER;
        }
        String descrip = Normalizer.normalize(rol.getDescrip(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (descrip.contains("admin")) {
            return ADMIN;
        }
        return USER;
    }
}
