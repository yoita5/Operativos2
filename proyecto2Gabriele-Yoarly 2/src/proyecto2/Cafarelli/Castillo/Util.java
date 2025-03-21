/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;

/**
 *
 *
 */

// funciones utiles 
public class Util {
    public String extraerNombreArchivo(String infoArchivo) {
        int indiceFin = infoArchivo.indexOf(" ["); // Buscar el inicio de los detalles
        if (indiceFin != -1) {
            return infoArchivo.substring(0, indiceFin).trim(); // Extraer nombre y eliminar espacios
        }
        return infoArchivo.trim(); // Si no encuentra " [", devolver string sin espacios extra
    }

   
}
