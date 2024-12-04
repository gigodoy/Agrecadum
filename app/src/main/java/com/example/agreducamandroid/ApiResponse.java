package com.example.agreducamandroid;

public class ApiResponse {
    private String status;
    private String message;
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String codigo;
        private String numero_orden;
        private String longitud_origen;
        private String latitud_origen;
        private String tipo_movimiento;
        private String patente_tracto;
        private String patente_remolque;

        public String getCodigo() {
            return codigo;
        }
        public String getNumeroOrden() {
            return numero_orden;
        }

        public String getLongitud() {
            return longitud_origen;
        }

        public String getLatitud() {
            return latitud_origen;
        }

        public String getTipoMovimiento() {
            return tipo_movimiento;
        }

        public String getPatenteTracto() {
            return patente_tracto;
        }

        public String getPatenteRemolque() {
            return patente_remolque;
        }

        public void setNumeroOrden(String numero_orden ) {
            this.numero_orden = numero_orden;
        }

        public void setLongitud(String longitud_origen) {
            this.longitud_origen = longitud_origen;
        }

        public void setLatitud(String latitud_origen) {
            this.latitud_origen = latitud_origen;
        }

        public void setTipoMovimiento(String tipo_movimiento) {
            this.tipo_movimiento = tipo_movimiento;
        }

        public void setPatenteTracto(String patente_tracto) {
            this.patente_tracto = patente_tracto;
        }

        public void setPatenteRemolque(String patente_remolque) {
            this.patente_remolque = patente_remolque;
        }
        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }
}
