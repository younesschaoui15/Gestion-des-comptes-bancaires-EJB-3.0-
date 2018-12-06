package metier.entities;

import java.util.Date;

import javax.persistence.*;

@Entity 
@DiscriminatorValue("C") 
public class CompteCourant extends Compte
{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double decouvert; 
	
	// Constructeur sans param et avec params 
	public CompteCourant() {
		super();
	} 
	public CompteCourant(Date d, double solde, double dec)  
	{
		super(d, solde);
        decouvert = dec;
	}
	
	// Getters et Setters 	
	public double getDecouvert() {
		return this.decouvert;
	}

	public void setDecouvert(double decouvert) {
		this.decouvert = decouvert;
	}
   
}
