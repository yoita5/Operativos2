/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;

/**
 *
 * 
 */
public class ListaEnlazadaBloques {
    private Bloque cabeza; // Primer bloque de la lista

    public ListaEnlazadaBloques() {
        this.cabeza = null;
    }

    // Agregar un bloque enlazándolo al final de la lista
    public void agregarBloque(Bloque nuevoBloque) {
        if (cabeza == null) {
            cabeza = nuevoBloque; // Si es el primer bloque, lo asignamos
        } else {
            Bloque actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoBloque; // Enlazamos el nuevo bloque
        }
    }

    // Mostrar los bloques enlazados
    public void mostrarLista() {
        if (cabeza == null) {
            System.out.println("No hay bloques asignados.");
            return;
        }

        Bloque actual = cabeza;
        System.out.print("Bloques: ");
        while (actual != null) {
            System.out.print(actual.id + " -> ");
            actual = actual.siguiente;
        }
        System.out.println("null");
    }

    public Bloque getPrimerBloque() {
        return cabeza;
    }
    
    public boolean contieneBloque(Bloque bloque) {
    Bloque actual = cabeza;
    while (actual != null) {
        if (actual == bloque) {
            return true; // El bloque pertenece a esta lista
        }
        actual = actual.siguiente;
    }
    return false; // No se encontró el bloque en esta lista
}

}
