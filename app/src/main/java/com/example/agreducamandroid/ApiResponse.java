package com.example.agreducamandroid;
public class ApiResponse {
    private String status; // Estado de la respuesta (ejemplo: "success" o "error")
    private Data data;     // Datos relacionados con la tarea




    // Clase interna para encapsular los datos de la tarea
    public static class Data {
        private String nombre_conductor;        // Nombre del conductor
        private String tipo_movimiento;   // Tipo de movimiento
        private String numero_orden;      // Número de orden
        private String patente_tracto;    // Patente del tractor
        private String patente_remolque;  // Patente del remolque

        // Getters y setters
        public String getConductor() {
            return nombre_conductor;
        }

        public void setConductor(String nombre_conductor) {
            this.nombre_conductor = nombre_conductor;
        }

        public String getTipoMovimiento() {
            return tipo_movimiento;
        }

        public void setTipoMovimiento(String tipoMovimiento) {
            this.tipo_movimiento = tipoMovimiento;
        }

        public String getNumeroOrden() {
            return numero_orden;
        }

        public void setNumeroOrden(String numero_orden) {
            this.numero_orden = numero_orden;
        }

        public String getPatenteTracto() {
            return patente_tracto;
        }

        public void setPatenteTracto(String patente_tracto) {
            this.patente_tracto = patente_tracto;
        }

        public String getPatenteRemolque() {
            return patente_remolque;
        }

        public void setPatenteRemolque(String patente_remolque) {
            this.patente_remolque = patente_remolque;
        }
    }

    // Getters y setters para ApiResponse
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
