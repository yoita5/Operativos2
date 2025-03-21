/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;

/**
 *
 * 
 */

public class Bloque {
    public int id;          // Identificador del bloque (posición en el array)
    public Bloque siguiente;  // Referencia directa al siguiente bloque (null si es el último)
    public boolean ocupado; // Estado del bloque (true = usado, false = libre)

    public Bloque(int id) {
        this.id = id;
        this.siguiente = null;  // Por defecto, no tiene enlace
        this.ocupado = false;
    }
}

