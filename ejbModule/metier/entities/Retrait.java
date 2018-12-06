package metier.entities;

import java.util.Date;

import javax.persistence.*;

@Entity 
@DiscriminatorValue("R") 
public class Retrait extends Operation 
{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructeur sans param et avec params 
	public Retrait() {}
	public Retrait(Date d, double m) {
        super(d, m);
    }
} 
