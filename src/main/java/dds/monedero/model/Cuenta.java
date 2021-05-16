package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private double limiteExtraccion = 1000;

  public void poner(double monto) {	  
	  validarExceso3DepositosDiarios();
	  validarMontoDebeSerPositivo(monto);
	  this.movimientos.add(new Deposito(LocalDate.now(), monto, true));
	  this.saldo += monto;
  }

  public void sacar(double monto) {
	  validarMontoDebeSerPositivo(monto);
	  validarMontoMayorQueSaldo(monto);
	  validarSuperaLimiteExtraccion(monto);
	  this.movimientos.add(new Extraccion(LocalDate.now(), monto, true));
	  this.saldo -= monto;
  }

  
  public void validarMontoDebeSerPositivo(double monto) {
	  if (monto <= 0) {
	      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
	    }
  }  
  public void validarExceso3DepositosDiarios() {
      if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }  
  public void validarMontoMayorQueSaldo(double monto) {
	  if (getSaldo() - monto < 0) {
	      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
	    }
  }  
  public void validarSuperaLimiteExtraccion(double monto) {
    if (monto > this.limiteExtraccion) {
    throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + this.limiteExtraccion);
    }
  }
  
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
