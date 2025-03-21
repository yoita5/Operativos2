/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *

 */
import javax.swing.*;
import java.awt.*;

import proyecto2.Cafarelli.Castillo.Archivo;
import proyecto2.Cafarelli.Castillo.Bloque;
import proyecto2.Cafarelli.Castillo.SistemaArchivos;

import java.util.HashMap;
import java.util.Random;

public class PanelDisco extends JPanel {
    private SistemaArchivos sistemaArchivos;
    private HashMap<Archivo, Color> coloresArchivos; // Mapa para almacenar los colores de cada archivo
    private Random random;

    public PanelDisco(SistemaArchivos sistemaArchivos) {
        this.sistemaArchivos = sistemaArchivos;
        this.coloresArchivos = new HashMap<>();
        this.random = new Random();
    }

    private Color obtenerColorArchivo(Archivo archivo) {
        // Si el archivo ya tiene un color asignado, reutilizarlo
        if (coloresArchivos.containsKey(archivo)) {
            return coloresArchivos.get(archivo);
        }

        // Si no tiene un color, asignar uno nuevo aleatorio y guardarlo
        Color nuevoColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        coloresArchivos.put(archivo, nuevoColor);
        return nuevoColor;
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (sistemaArchivos == null) return;

    int bloqueSize = 50;
    int padding = 20;
    int cols = 5;
    int totalBloques = sistemaArchivos.getNumeroBloques();

    System.out.println("Redibujando disco...");

    // 📌 1️⃣ Pintar TODOS los bloques en GRIS como "vacíos"
    for (int i = 0; i < totalBloques; i++) {
        int x = (i % cols) * (bloqueSize + padding);
        int y = (i / cols) * (bloqueSize + padding);

        g.setColor(Color.LIGHT_GRAY); // Color para bloques libres
        g.fillRect(x, y, bloqueSize, bloqueSize);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, bloqueSize, bloqueSize);
        g.drawString(String.valueOf(i), x + 15, y + 30);
    }

    //Ahora pintar los bloques que están **asignados a archivos**
 
    Archivo archivoActual = sistemaArchivos.getListaArchivos().getCabeza(); // Primer archivo

    while (archivoActual != null) { // Recorrer archivos
        Color colorArchivo = obtenerColorArchivo(archivoActual);
        Bloque bloqueActual = archivoActual.listaBloques.getPrimerBloque(); // Primer bloque del archivo

        //  PROTECCIÓN: Si el archivo no tiene bloques, evitar entrar al bucle
        if (bloqueActual == null) {
            archivoActual = archivoActual.siguiente;
            continue;
        }

        while (bloqueActual != null) { // Recorrer la lista de bloques del archivo
            int x = (bloqueActual.id % cols) * (bloqueSize + padding);
            int y = (bloqueActual.id / cols) * (bloqueSize + padding);

            g.setColor(colorArchivo);
            g.fillRect(x, y, bloqueSize, bloqueSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, bloqueSize, bloqueSize);
            g.drawString(String.valueOf(bloqueActual.id), x + 18, y + 30);
            
            
             if (bloqueActual.siguiente != null) {
                int x2 = (bloqueActual.siguiente.id % cols) * (bloqueSize + padding);
                int y2 = (bloqueActual.siguiente.id / cols) * (bloqueSize + padding);

                // **Si la flecha apunta a la izquierda, no dibujarla**
                if (x2 > x || y2 > y) {
                    dibujarFlecha(g, x + bloqueSize - 5, y + bloqueSize / 2, x2 + 5, y2 + bloqueSize / 2);
                }
            }
            
            
            //  PROTECCIÓN: Asegurar que el siguiente bloque no genere un bucle infinito
            Bloque temp = bloqueActual;
            bloqueActual = bloqueActual.siguiente;
            if (bloqueActual == temp) { // Si un bloque se apunta a sí mismo, corregirlo
               
                bloqueActual = null; // Romper el bucle
            }
        }

        archivoActual = archivoActual.siguiente; // Avanzar en la lista de archivos
    }
}

    public void actualizarDisco() {
    System.out.println("Actualizando disco...");
    repaint(); // Solo repinta cuando es necesario
}
    
    private void dibujarFlecha(Graphics g, int x1, int y1, int x2, int y2) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setStroke(new BasicStroke(2)); // Grosor de la línea

    // Dibujar la línea principal
    g2d.drawLine(x1, y1, x2, y2);

    // Calcular dirección de la flecha
    double angle = Math.atan2(y2 - y1, x2 - x1);
    int arrowSize = 10; // Tamaño de la flecha ajustado

    // Coordenadas para la punta de la flecha
    int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
    int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
    int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
    int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

    // Dibujar las líneas de la punta de la flecha
    g2d.drawLine(x2, y2, xArrow1, yArrow1);
    g2d.drawLine(x2, y2, xArrow2, yArrow2);
}
}
