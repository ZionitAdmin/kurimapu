package cl.smapdev.curimapu.clases.utilidades;

import java.util.ArrayList;
import java.util.List;

import cl.smapdev.curimapu.MainActivity;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembra;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembraEvento;

/**
 * TICKET 2494 y 2515 - 2026-10-07: los eventos de siembra viajan dentro de cada checklist de siembra (lista
 * @Ignore "eventos_siembra"), y solo se adjuntaban en el menu "Subir" de FragmentCheckList. La subida desde la
 * pantalla principal y la subida de un checklist desde su fila los mandaban sin eventos. Estos metodos hacen lo mismo
 * que el menu "Subir" para esas dos rutas.
 */
public class EventosSiembraSubida {

    private EventosSiembraSubida() {
    }

    /**
     * Un checklist ya sincronizado puede tener eventos nuevos/editados sin sincronizar (estado_sincronizacion = 0).
     * Se agrega su cabecera a la lista para que viaje junto con esos eventos (igual que el menu "Subir").
     */
    public static void agregarChecklistsConEventosPendientes(List<CheckListSiembra> checklists) {
        List<CheckListSiembraEvento> eventosPendientes = MainActivity.myAppDB.DaoClSiembra().getEventosToSync();
        if (eventosPendientes == null || eventosPendientes.isEmpty()) {
            return;
        }

        List<String> clavesYaIncluidas = new ArrayList<>();
        for (CheckListSiembra c : checklists) {
            clavesYaIncluidas.add(c.getClave_unica());
        }

        List<String> clavesConEventoPendiente = new ArrayList<>();
        for (CheckListSiembraEvento evento : eventosPendientes) {
            if (!clavesConEventoPendiente.contains(evento.getClave_unica_cl_siembra())) {
                clavesConEventoPendiente.add(evento.getClave_unica_cl_siembra());
            }
        }

        for (String claveUnica : clavesConEventoPendiente) {
            if (!clavesYaIncluidas.contains(claveUnica)) {
                CheckListSiembra header = MainActivity.myAppDB.DaoClSiembra().getCLSiembraByClaveUnica(claveUnica);
                if (header != null) {
                    checklists.add(header);
                }
            }
        }
    }

    /**
     * Adjunta a cada checklist la lista de sus eventos (se serializa en el JSON como "eventos_siembra").
     */
    public static void adjuntarEventos(List<CheckListSiembra> checklists) {
        for (CheckListSiembra clSiembra : checklists) {
            List<CheckListSiembraEvento> eventos = MainActivity.myAppDB
                    .DaoClSiembra()
                    .getEventosByClaveUnicaClSiembra(clSiembra.getClave_unica());
            clSiembra.setEventos_siembra(eventos);
        }
    }
}
