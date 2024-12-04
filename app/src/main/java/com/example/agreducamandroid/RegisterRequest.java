package com.example.agreducamandroid;

public class RegisterRequest {
    private String nombre_completo;
    private String rut;
    private String telefono;
    private String email;
    private String password;

    public RegisterRequest(String nombre_completo, String rut, String telefono, String email, String password) {
        this.nombre_completo = nombre_completo;
        this.rut = rut;
        this.telefono = telefono;
        this.email = email;
        this.password = password;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
