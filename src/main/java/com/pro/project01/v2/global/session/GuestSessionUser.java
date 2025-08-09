package com.pro.project01.v2.global.session;

public class GuestSessionUser
{
    private final String username;
    private final boolean guest = true;

    public GuestSessionUser(String username)
    {
        this.username = username;
    }

    public String getUsername() { return username; }
    public boolean isGuest() { return guest; }
}
