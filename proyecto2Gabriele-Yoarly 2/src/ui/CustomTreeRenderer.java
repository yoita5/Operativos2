/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *

*
 */
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class CustomTreeRenderer extends DefaultTreeCellRenderer {
    private Icon folderIcon;
    private Icon fileIcon;

    public CustomTreeRenderer() {
        folderIcon = UIManager.getIcon("FileView.directoryIcon"); // Ícono de carpeta
        fileIcon = UIManager.getIcon("FileView.fileIcon"); // Ícono de archivo
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getAllowsChildren()) {
            setIcon(folderIcon); // Directorios
        } else {
            setIcon(fileIcon); // Archivos
        }
        return this;
    }
}
