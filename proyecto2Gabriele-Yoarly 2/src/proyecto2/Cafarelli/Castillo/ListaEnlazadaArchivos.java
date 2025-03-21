/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;

/**
 *
 * 
 */
public class ListaEnlazadaArchivos {
    private Archivo cabeza; // Primer archivo de la lista

    public ListaEnlazadaArchivos() {
        this.cabeza = null;
    }

    // ✅ Agregar un archivo al final de la lista
    public void agregar(Archivo nuevoArchivo) {
        if (cabeza == null) {
            cabeza = nuevoArchivo;
        } else {
            Archivo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoArchivo; // Enlazar el nuevo archivo
        }
    }
    
    // ✅ Eliminar un archivo de la lista enlazada
    public void eliminar(Archivo archivo) {
        if (cabeza == null) return; // Si la lista está vacía, no hay nada que eliminar

        if (cabeza == archivo) { 
            cabeza = cabeza.siguiente; // Si el archivo a eliminar es la cabeza, mover la cabeza al siguiente
            archivo.siguiente = null; // Romper el enlace
            return;
        }

        Archivo actual = cabeza;
        while (actual.siguiente != null && actual.siguiente != archivo) {
            actual = actual.siguiente;
        }

        if (actual.siguiente == archivo) {
            actual.siguiente = archivo.siguiente; // Saltar el nodo para eliminarlo de la lista
            archivo.siguiente = null; // Romper el enlace
        }
    }


    // ✅ Mostrar los archivos en la lista
    public void mostrarArchivos() {
        Archivo actual = cabeza;
        if (actual == null) {
            System.out.println("No hay archivos en el sistema.");
            return;
        }
        System.out.println("Archivos en el sistema:");
        while (actual != null) {
            System.out.println(" - " + actual.nombre + " (Tamaño: " + actual.tamano + " bloques)");
            actual = actual.siguiente;
        }
    }

    public Archivo getCabeza() {
        return cabeza;
    }
}

