/*
 * INTERFACES
 * Define los contratos que deben cumplir las implementaciones concretas.
 * Permite que CuentaBancaria dependa de abstracciones, no de implementaciones
 * específicas. Esto cumple con el principio de Dependency Inversion (DIP).
 */

/**
 * Interfaz para notificaciones bancarias.
 * Cualquier medio de notificación (email, SMS, etc) debe implementar esto.
 * Cumple con Interface Segregation Principle (ISP).
 */
interface NotificadorBancario {
    void enviarNotificacion(String destinatario, String mensaje);
}

/**
 * Interfaz para la persistencia de datos.
 * Abstrae la forma en que se guardan los datos de la cuenta.
 */
interface RepositorioCuenta {
    void guardar(CuentaBancaria cuenta);
    void actualizar(CuentaBancaria cuenta);
}

/**
 * Interfaz para la presentación de detalles.
 * Abstrae cómo se muestran los detalles de la cuenta al usuario.
 */
interface PresentadorDetallesCuenta {
    void mostrar(CuentaBancaria cuenta);
}

/*
 * IMPLEMENTACIONES CONCRETAS
 * Cada clase implementa una interfaz y tiene una responsabilidad única.
 */

/**
 * Implementación concreta para enviar notificaciones por correo electrónico.
 * Cumple con Single Responsibility Principle (SRP) al tener solo una razón
 * para cambiar: si cambia la forma de enviar correos.
 */
class NotificadorEmail implements NotificadorBancario {
    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        System.out.println("Enviando correo a " + destinatario + ": " + mensaje);
    }
}

/**
 * Implementación concreta para enviar notificaciones por SMS.
 * Es un ejemplo del Open/Closed Principle (OCP): se puede agregar sin
 * modificar código existente.
 */
class NotificadorSMS implements NotificadorBancario {
    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

/**
 * Implementación de repositorio que almacena datos en memoria.
 * En una aplicación real, esto podría conectarse a una base de datos.
 */
class RepositorioCuentaEnMemoria implements RepositorioCuenta {
    @Override
    public void guardar(CuentaBancaria cuenta) {
        System.out.println("Cuenta guardada en base de datos");
    }

    @Override
    public void actualizar(CuentaBancaria cuenta) {
        System.out.println("Cuenta actualizada en base de datos");
    }
}

/**
 * Presentador responsable de mostrar los detalles de una cuenta en la consola.
 * Cumple con SRP al tener solo una responsabilidad: presentar información.
 */
class PresentadorConsolaDetalles implements PresentadorDetallesCuenta {
    @Override
    public void mostrar(CuentaBancaria cuenta) {
        System.out.println("\nDetalles de la Cuenta");
        System.out.println("Titular: " + cuenta.getTitular());
        System.out.println("ID de Cuenta: " + cuenta.getIdCuenta());
        System.out.println("Saldo Actual: $" + cuenta.getSaldo());
        System.out.println();
    }
}

/*
 * CLASE PRINCIPAL DE DOMINIO
 * Contiene la lógica central de la aplicación bancaria.
 * Cumple con SRP al ser responsable solo de operaciones bancarias.
 * No conoce detalles sobre cómo se envían notificaciones, cómo se guardan
 * datos, o cómo se presentan. Esto cumple con DIP: depende de abstracciones.
 */

/**
 * Gestiona una cuenta bancaria.
 * Responsable únicamente de la lógica de operaciones bancarias.
 * Las dependencias se inyectan mediante el constructor.
 */
class CuentaBancaria {
    private String titular;
    private String idCuenta;
    private double saldo;
    
    private NotificadorBancario notificador;
    private RepositorioCuenta repositorio;

    /**
     * Constructor que recibe las dependencias necesarias.
     * La inyección de dependencias permite que CuentaBancaria no esté
     * acoplada a implementaciones específicas. Cumple con DIP.
     */
    public CuentaBancaria(
        String titular,
        String idCuenta,
        double saldo,
        NotificadorBancario notificador,
        RepositorioCuenta repositorio
    ) {
        this.titular = titular;
        this.idCuenta = idCuenta;
        this.saldo = saldo;
        this.notificador = notificador;
        this.repositorio = repositorio;
        
        repositorio.guardar(this);
    }

    /**
     * Realiza un depósito a la cuenta.
     * Actualiza el saldo, notifica al usuario y persiste los cambios.
     */
    public void depositar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        saldo += monto;
        notificador.enviarNotificacion(titular, "Depósito exitoso de $" + monto);
        repositorio.actualizar(this);
        System.out.println("Depositado: $" + monto);
    }

    /**
     * Realiza un retiro de la cuenta.
     * Valida que el saldo sea suficiente antes de proceder.
     */
    public void retirar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        if (saldo >= monto) {
            saldo -= monto;
            notificador.enviarNotificacion(titular, "Retiro exitoso de $" + monto);
            repositorio.actualizar(this);
            System.out.println("Retirado: $" + monto);
        } else {
            notificador.enviarNotificacion(titular, "Intento de retiro rechazado - Saldo insuficiente");
            System.out.println("Saldo insuficiente. Disponible: $" + saldo);
        }
    }

    /**
     * Obtiene el nombre del titular de la cuenta.
     */
    public String getTitular() {
        return titular;
    }

    /**
     * Obtiene el identificador único de la cuenta.
     */
    public String getIdCuenta() {
        return idCuenta;
    }

    /**
     * Obtiene el saldo actual de la cuenta.
     */
    public double getSaldo() {
        return saldo;
    }
}

/*
 * APLICACIÓN PRINCIPAL
 * Punto de entrada que orquesta todas las dependencias.
 */

public class AplicacionBancaria {
    public static void main(String[] args) {
        /*
         * Crear las dependencias concretas.
         * La inyección de dependencias permite cambiar implementaciones fácilmente.
         * Por ejemplo, cambiar de email a SMS solo requiere cambiar una línea.
         */
        NotificadorBancario notificador = new NotificadorEmail();
        RepositorioCuenta repositorio = new RepositorioCuentaEnMemoria();
        PresentadorDetallesCuenta presentador = new PresentadorConsolaDetalles();

        /*
         * Crear la cuenta inyectando las dependencias.
         * CuentaBancaria no sabe cómo se envían las notificaciones ni cómo
         * se persisten los datos. Solo usa las abstracciones.
         */
        CuentaBancaria cuenta = new CuentaBancaria(
            "Pepe",
            "12345678",
            1000,
            notificador,
            repositorio
        );

        /*
         * Realizar operaciones bancarias.
         * Las notificaciones se envían automáticamente a través
         * del notificador inyectado.
         */
        System.out.println("Operaciones con primera cuenta:");
        cuenta.depositar(500);
        cuenta.retirar(200);
        presentador.mostrar(cuenta);

        /*
         * Ejemplo de cambio de implementación sin modificar CuentaBancaria.
         * Ahora usamos notificaciones por SMS en lugar de email.
         * Esto demuestra el Open/Closed Principle.
         */
        System.out.println("Operaciones con segunda cuenta (usando SMS):");
        NotificadorBancario notificadorSMS = new NotificadorSMS();
        CuentaBancaria cuenta2 = new CuentaBancaria(
            "Maria",
            "87654321",
            5000,
            notificadorSMS,
            repositorio
        );
        cuenta2.retirar(1000);
        presentador.mostrar(cuenta2);
    }
}
