package cl.smapdev.curimapu.clases.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cl.smapdev.curimapu.R;
import cl.smapdev.curimapu.clases.relaciones.AnexoOgmPropio;

/*
 * TICKET 2512 - 2026-09-29
 * Lista de la pantalla "Ver OGM propios". Solo muestra, no tiene acciones al tocar.
 */
public class OgmPropiosAdapter extends RecyclerView.Adapter<OgmPropiosAdapter.FilaVH> {

    public static class Fila {
        final AnexoOgmPropio anexo;
        final AnexoOgmPropio.UltimaVisita visita;  // null = sin visita
        final String etiquetaCuatrimestre;         // C1 / C2 / C3 de la visita mostrada

        public Fila(AnexoOgmPropio anexo, AnexoOgmPropio.UltimaVisita visita, String etiquetaCuatrimestre) {
            this.anexo = anexo;
            this.visita = visita;
            this.etiquetaCuatrimestre = etiquetaCuatrimestre;
        }
    }

    private final List<Fila> filas = new ArrayList<>();

    public void setFilas(List<Fila> nuevas) {
        filas.clear();
        if (nuevas != null) filas.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilaVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ogm_propio, parent, false);
        return new FilaVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaVH holder, int position) {
        holder.bind(filas.get(position));
    }

    @Override
    public int getItemCount() {
        return filas.size();
    }

    static class FilaVH extends RecyclerView.ViewHolder {
        private final View franja;
        private final TextView anexo;
        private final TextView estado;
        private final TextView agricultor;
        private final TextView comuna;
        private final TextView especie;
        private final TextView temporada;
        private final TextView cuatrimestre;
        private final TextView ultimaVisita;

        FilaVH(@NonNull View itemView) {
            super(itemView);
            franja = itemView.findViewById(R.id.ogm_item_franja);
            anexo = itemView.findViewById(R.id.ogm_item_anexo);
            estado = itemView.findViewById(R.id.ogm_item_estado);
            agricultor = itemView.findViewById(R.id.ogm_item_agricultor);
            comuna = itemView.findViewById(R.id.ogm_item_comuna);
            especie = itemView.findViewById(R.id.ogm_item_especie);
            temporada = itemView.findViewById(R.id.ogm_item_temporada);
            cuatrimestre = itemView.findViewById(R.id.ogm_item_cuatrimestre);
            ultimaVisita = itemView.findViewById(R.id.ogm_item_ultima_visita);
        }

        void bind(Fila fila) {
            AnexoOgmPropio a = fila.anexo;
            anexo.setText(a.numAnexo);
            mostrar(agricultor, a.agricultor);
            mostrar(comuna, a.comuna);
            mostrar(especie, a.especie);
            mostrar(temporada, a.temporada);

            int colorFranja;
            int colorTexto;
            int colorFondo;
            int icono;
            if (fila.visita == null) {
                estado.setText("Sin visita");
                icono = R.drawable.ic_ogm_sin_visita;
                colorFranja = R.color.ogm_rojo;
                colorTexto = R.color.ogm_rojo;
                colorFondo = R.color.ogm_rojo_bg;
                mostrar(cuatrimestre, null);
                mostrar(ultimaVisita, null);
            } else {
                mostrar(cuatrimestre, fila.etiquetaCuatrimestre);
                mostrar(ultimaVisita, "Últ. visita " + voltearFecha(fila.visita.fecha));
                colorFranja = R.color.ogm_verde_ok;
                if (fila.visita.esCpv()) {
                    estado.setText("CPV");
                    icono = R.drawable.ic_ogm_cpv;
                    colorTexto = R.color.ogm_ambar;
                    colorFondo = R.color.ogm_ambar_bg;
                } else {
                    estado.setText("SPV");
                    icono = R.drawable.ic_ogm_spv;
                    colorTexto = R.color.ogm_verde_ok;
                    colorFondo = R.color.ogm_verde_ok_bg;
                }
            }

            estado.setCompoundDrawablesRelativeWithIntrinsicBounds(icono, 0, 0, 0);
            franja.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), colorFranja)));
            estado.setTextColor(ContextCompat.getColor(itemView.getContext(), colorTexto));
            estado.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), colorFondo)));
        }

        // oculta el dato (con su icono) si viene vacio
        private static void mostrar(TextView tv, String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                tv.setVisibility(View.GONE);
            } else {
                tv.setText(texto.trim());
                tv.setVisibility(View.VISIBLE);
            }
        }

        // yyyy-MM-dd -> dd-MM-yyyy
        private static String voltearFecha(String fecha) {
            if (fecha == null || fecha.length() < 10) return (fecha != null) ? fecha : "";
            return fecha.substring(8, 10) + "-" + fecha.substring(5, 7) + "-" + fecha.substring(0, 4);
        }
    }
}
