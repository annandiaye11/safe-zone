package com.cgl.userservice.data.enums;

public enum Role {
    CLIENT,
    SELLER;

    public static Role getRole(String nomRole) {
        String nom = nomRole.toLowerCase();

        if (nom.equalsIgnoreCase("client")) {
            return Role.CLIENT;
        }

        if (nom.equalsIgnoreCase("seller")) {
            return Role.SELLER;
        }

        return null;
    }
}
