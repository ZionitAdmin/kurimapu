package cl.smapdev.curimapu.clases.utilidades;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.exifinterface.media.ExifInterface;

import cl.smapdev.curimapu.R;

public class CameraUtils {

    /**
     * TICKET 2515 (extra) - 2026-10-16 - Deja la foto derecha segun la orientacion (EXIF) con que la guardo la camara.
     * Antes se hacia decodeFile + compress sin leer el EXIF: el archivo quedaba con los pixeles del sensor y sin la marca
     * de rotacion, por lo que las fotos tomadas con el telefono girado (horizontal) quedaban guardadas al reves.
     * Mismo metodo que ya usa la APK de vegetables (Ticket 2460, punto 17, 2026-10-14).
     * Debe llamarse ANTES de reescribir el archivo (el EXIF solo existe mientras el archivo es el original de la camara).
     * Si no hay rotacion que aplicar o algo falla (incluida la falta de memoria), devuelve el mismo bitmap sin cambios.
     */
    public static Bitmap corregirOrientacion(Bitmap bm, String rutaArchivo) {
        if (bm == null || rutaArchivo == null) return bm;

        try {
            ExifInterface exif = new ExifInterface(rutaArchivo);
            int orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int grados = 0;
            if (orientacion == ExifInterface.ORIENTATION_ROTATE_90) {
                grados = 90;
            } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_180) {
                grados = 180;
            } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_270) {
                grados = 270;
            }

            if (grados == 0) return bm;

            Matrix matriz = new Matrix();
            matriz.postRotate(grados);
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matriz, true);
        } catch (Exception e) {
            return bm;
        } catch (OutOfMemoryError e) {
            return bm;
        }
    }


    public static Bitmap escribirFechaImg(Bitmap bm, Activity activity){

        // TICKET 2515 (extra) - 2026-10-16: si el bitmap ya es mutable (FragmentFormVisitas lo decodifica asi) se dibuja la
        // fecha directamente sobre el, sin crear una segunda copia completa en memoria. Si no lo es (FragmentCreaFicha),
        // se mantiene el comportamiento de siempre: copia mutable.
        Bitmap dest = (bm.isMutable() && bm.getConfig() == Bitmap.Config.ARGB_8888)
                ? bm
                : Bitmap.createBitmap(bm,0, 0,bm.getWidth(), bm.getHeight()).copy(Bitmap.Config.ARGB_8888, true);

        String fecha = Utilidades.fechaActualInvSinHora();

        Canvas cs = new Canvas(dest);

//        Paint myPaint = new Paint();
//        myPaint.setColor(activity.getResources().getColor(R.color.transparentBlack));
//        myPaint.setStrokeWidth(10);
//        cs.drawRect(5, dest.getHeight() - 90, 700, dest.getHeight() - 10, myPaint);

        Paint tPaint = new Paint();
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(activity.getResources().getColor(R.color.colorGold));

        tPaint.setTextSize(calculateTextSize(bm.getWidth()));
        tPaint.setStrokeWidth(10);

        // text shadow
        tPaint.setShadowLayer(1f, 0f, 1f, activity.getResources().getColor(android.R.color.black));

        Rect bounds = new Rect();
        tPaint.getTextBounds(fecha, 0, fecha.length(), bounds);

        cs.drawText(fecha,7, dest.getHeight() - 25 , tPaint);



        return dest;
    }

    private static int calculateTextSize(int width){


        if(width <= 600){
            return 15;
        }

        if(width <= 1400){
            return 30;
        }

        return 50;
    }
}
