package com.example.firebaseconn;

public class Producto {

    private String nombre, descripcion, imageUrl, mkey;
    private double precio;

    public Producto(String nombre, String descripcion, String imageUrl, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imageUrl = imageUrl;
        this.precio = precio;
    }

    public Producto() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", precio=" + precio +
                '}';
    }
}
