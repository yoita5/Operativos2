/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2.Cafarelli.Castillo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * 
 */

//comment commit;

// clase que maneja la asignacion de bloques a archivos

public class SistemaArchivos {

    public static final int NUMERO_BLOQUES = 35; // Número fijo de bloques
    public boolean[] bitmap; // Bitmap para gestionar bloques libres
    public Bloque[] bloques; // Array de bloques del disco
    public int bloquesLibres; // Contador de bloques disponibles
    public ListaEnlazadaArchivos archivos; // Lista enlazada de archivos
    private static final String RUTA_JSON = "src/proyecto2Gabriele-Yoarly 3"; // Ruta dentro de src/proyecto2so


    public SistemaArchivos() {
        this.bitmap = new boolean[NUMERO_BLOQUES]; // Inicialmente, todos los bloques están libres
        this.bloques = new Bloque[NUMERO_BLOQUES];
        this.bloquesLibres = NUMERO_BLOQUES; // Todos los bloques están libres al inicio
        this.archivos = new ListaEnlazadaArchivos();  //lista enlazada de archivos en el pryecto

        // Inicializar los bloques y el bitmap
        for (int i = 0; i < NUMERO_BLOQUES; i++) {
            bloques[i] = new Bloque(i);
            bitmap[i] = false; // Todos los bloques están libres al inicio
        }
    }
    
    public ListaEnlazadaArchivos getListaArchivos() {
        return archivos;
    }

    // Función para asignar bloques a un archivo y actualizar bloquesLibres
    public void asignarBloquesArchivo(Archivo archivo, int n) {
        if (n > bloquesLibres) {
            System.out.println("Error: No hay suficientes bloques libres.");
            return;
        }

        int contador = 0;
        Bloque anterior = null;

        for (int i = 0; i < NUMERO_BLOQUES && contador < n; i++) {
            if (!bitmap[i]) { // Bloque libre encontrado
                bitmap[i] = true; // Marcar como ocupado
                bloques[i].ocupado = true; // Actualizar estado del bloque
                bloquesLibres--; // Reducir el contador de bloques libres

                if (anterior != null) {
                    anterior.siguiente = bloques[i]; // Enlazar el bloque anterior con el nuevo
                }

                archivo.listaBloques.agregarBloque(bloques[i]); // Agregar a la lista enlazada del archivo
                anterior = bloques[i];
                contador++;
            }
        }

        if (contador < n) {
            System.out.println("Error: No hay suficientes bloques disponibles.");
        } else {
            System.out.println("Archivo '" + archivo.nombre + "' asignado con " + n + " bloques.");
        }
    }

    //  Función para liberar los bloques de un archivo y actualizar bloquesLibres
    public void liberarBloquesArchivo(Archivo archivo) { 
    Bloque actual = archivo.listaBloques.getPrimerBloque();

    while (actual != null) {
        int id = actual.id;

        if (bitmap[id]) { // 🔹 Solo incrementar si el bloque estaba ocupado
            bitmap[id] = false; // Marcar como libre en el bitmap
            bloques[id].ocupado = false; // Actualizar estado del bloque
            bloquesLibres++; // Aumentar el contador de bloques libres
        }

        Bloque siguiente = actual.siguiente;
        bloques[id].siguiente = null; // 🔹 Romper la conexión dentro del array de bloques
        actual.siguiente = null; // 🔹 Romper la referencia en el objeto actual

        actual = siguiente;
    }

    archivo.listaBloques = new ListaEnlazadaBloques(); // Resetear la lista del archivo
    System.out.println("Se liberaron los bloques del archivo '" + archivo.nombre + "'.");
}
    //  Método para obtener la cantidad de bloques libres
    public int getBloquesLibres() {
        return bloquesLibres;
    }

    // ✅ Función para agregar un archivo a la lista enlazada de archivos
    public void agregarArchivo(Archivo archivo) {
        archivos.agregar(archivo);
        System.out.println("Archivo '" + archivo.nombre + "' agregado al sistema.");
    }
    
    //funcion para eliminar un archivo de la lista enlazada de archivos
    public void eliminarArchivo(Archivo archivo) {
        archivos.eliminar(archivo); // Llamar a la función de eliminar en la lista enlazada
        System.out.println("Archivo '" + archivo.nombre + "' eliminado del sistema.");
}

    
    //  Función para buscar un archivo en la lista enlazada por nombre
    public Archivo buscarArchivo(String nombre) {
        Archivo actual = archivos.getCabeza(); // Obtener el primer archivo de la lista

        while (actual != null) {
            if (actual.nombre.equals(nombre)) {
                return actual; // Se encontró el archivo
            }
            actual = actual.siguiente;
        }
        return null; // No se encontró el archivo
    } // Esta funcion retorna el archivo cuyo nombre es pasado por parametro 
    
    public void renombrarArchivo(Archivo archivo, String nuevoNombre) {
    if (archivo == null || nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
        System.out.println("Error: El archivo o el nuevo nombre no pueden ser nulos o vacíos.");
        return;
    }
    archivo.nombre = nuevoNombre; // Cambiar el nombre del archivo
    System.out.println("Archivo renombrado a: " + nuevoNombre);
}

    
    public Bloque[] getBloques() {
    return this.bloques;
     
    }
    
    public int getNumeroBloques() {
    return NUMERO_BLOQUES;
}

    public Bloque getBloque(int id) {
        return bloques[id]; // Retorna el bloque por su ID
    }

public Archivo getArchivoPorBloque(Bloque bloque) {
    Archivo actual = archivos.getCabeza();
    while (actual != null) {
        if (actual.listaBloques.contieneBloque(bloque)) {
            return actual;
        }
        actual = actual.siguiente;
    }
    return null; // No pertenece a ningún archivo
}


    public void guardarEstado() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File archivo = new File(RUTA_JSON);

        try {
            // Verificar si la carpeta "src/proyecto2so" existe antes de guardar
            archivo.getParentFile().mkdirs(); 

            FileWriter writer = new FileWriter(archivo);
            gson.toJson(this, writer);
            writer.close();
            System.out.println("Estado guardado en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ✅ Cargar el estado desde JSON dentro de src/proyecto2so/
    public static SistemaArchivos cargarEstado() {
        Gson gson = new Gson();
        File archivo = new File(RUTA_JSON);

        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("No hay estado previo. Iniciando nueva simulación.");
            return new SistemaArchivos();
        }

        try (FileReader reader = new FileReader(archivo)) {
            return gson.fromJson(reader, SistemaArchivos.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new SistemaArchivos();
        }
    }
}


