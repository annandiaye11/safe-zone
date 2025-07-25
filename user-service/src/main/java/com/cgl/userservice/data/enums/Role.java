package com.cgl.userservice.data.enums;

public enum Role {
    CLIENT,
    SELLER;

    public static Role getRole(String nomRole) {
        String nom = nomRole.toLowerCase();
        if (nom.equals("client")) {
            return Role.CLIENT;
        }
        if (nom.equals("seller")) {
            return Role.SELLER;
        }
        return null;
    }
}
