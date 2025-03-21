/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;

/**
 *
 * 
 */


public class Archivo {
    public String nombre;
    public int tamano; // Número de bloques asignados
    public ListaEnlazadaBloques listaBloques; // Lista enlazada de bloques del archivo
    public Archivo siguiente; // Referencia al siguiente archivo en la lista

    public Archivo(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.listaBloques = new ListaEnlazadaBloques(); // Inicializar lista de bloques
        this.siguiente = null; // Por defecto, no tiene enlace con otro archivo
    }

    //  Método para mostrar los bloques asignados al archivo
    public void mostrarBloques() {
        System.out.println("Archivo: " + nombre);
        listaBloques.mostrarLista();
    }
}
