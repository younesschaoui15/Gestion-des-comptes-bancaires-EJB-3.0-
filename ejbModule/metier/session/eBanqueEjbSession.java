package metier.session;

import java.net.ConnectException;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

import metier.entities.*;

/**
 * Session Bean implementation class eBanqueEjbSession
 */
@Stateless
public class eBanqueEjbSession implements eBanqueEjbSessionRemote, eBanqueEjbSessionLocal {
	
	//@PersistenceContext(name="UP_CATAL", unitName="PU_e-Banque") 
    @PersistenceContext
    private EntityManager em; 
    
    /** Default constructor */
    public eBanqueEjbSession() {}
    
    /** Implémentation des méthodes */
    
    @Override
    public Employe consulterAuthentification(String log, String passe)
    {	
    	Query query = em.createQuery("SELECT a FROM Auth a WHERE a.login like :l and a.password like :p");
        query.setParameter("l", log);
        query.setParameter("p", passe);
        Auth auth = (Auth) query.getSingleResult();
        if(auth == null)
        	throw new RuntimeException("Login ou mot de passe invalide!");
    	Employe emp = em.find(Employe.class, auth.getEmploye().getNumEmploye());
    	if(emp == null)
    		throw new RuntimeException("Employé invalide (null).");
    	
    	return emp;
    }
    
    @Override 
    public void addClient(Client c) { 
    	em.persist(c); 
    }
    
    @Override
    public List<Compte> consulterComptes(Long codeClient) 
    {
    	Query req = em.createQuery("select c from Compte c where c.client.codeClient =:code"); 
	    req.setParameter("code", codeClient); 
	    return req.getResultList(); 
	}

	@Override 
    public void addEmploye(Employe e, Long numEmpSup) 
    { 
	    Employe empSup; 
	    if(numEmpSup!=null)
	    { 
		    empSup=em.find(Employe.class, numEmpSup); 
		    e.setSupHierarchique(empSup); 
	    } 
	    em.persist(e); 
    } 
    
    @Override 
    public void addGroupe(Groupe g) 
    { 
    	em.persist(g); 
    } 
    
    @Override 
    public void addEmployeToGroupe(Long idGroupe, Long idEmp) 
    { 
	    Employe emp = em.find(Employe.class, idEmp); 
	    Groupe g = em.find(Groupe.class, idGroupe); 
	    emp.getGroupes().add(g); 
	    g.getEmployes().add(emp); 
    } 
     
    @Override 
    public void addCompte(Compte c, Long numCli, Long numEmp) 
    { 
	    Client cli = em.find(Client.class, numCli); 
	    Employe emp = em.find(Employe.class,numEmp); 
	    c.setClient(cli); 
	    c.setEmploye(emp); 
	    em.persist(c); 
    } 
    
    @Override 
    public void addOperation(Operation op, String numCpte, Long numEmp) 
    { 
	    Compte c = em.find(Compte.class, numCpte); 
	    Employe emp = em.find(Employe.class, numEmp); 
	    op.setEmploye(emp); 
	    
	    if(op instanceof Retrait)
	    {	
	    	if( c instanceof CompteEpargne )
    		{
	    		if( c.getSolde() >= op.getMontant() )
		    		c.setSolde(c.getSolde()-op.getMontant());
	    		else
	    	    	throw new RuntimeException();
    		}else
    			c.setSolde(c.getSolde()-op.getMontant());	    		
	    }
	    else
	    	c.setSolde(c.getSolde()+op.getMontant());
	    
	    op.setCompte(c); 
	    em.persist(op); 
    } 
    
    @Override 
    public Compte consulterCompte(String numCpte) 
    { 
	    Compte cpte = em.find(Compte.class, numCpte); 
	    if(cpte==null) 
	    	throw new RuntimeException("Compte "+numCpte+" n'existe pas"); 
	    cpte.getOperations().size(); 
	    return cpte; 
    } 
    
    @Override 
    public List<Client> consulterClientsParNom(String mc) 
    { 
	    Query req = em.createQuery("select c from Client c where c.nom like :mc"); 
	    req.setParameter("mc","%"+mc+"%");
	    return req.getResultList(); 
    } 
    
	@Override 
    public List<Client> consulterClients()
    { 
	    Query req = em.createQuery("select c from Client c"); 
	    return req.getResultList(); 
    } 
    
    @Override 
    public List<Groupe> consulterGroupes() 
    { 
	    Query req = em.createQuery("select g from Groupe g"); 
	    return req.getResultList(); 
    }

    @Override 
    public List<Employe> consulterEmployes() 
    { 
	    Query req = em.createQuery("select e from Employe e"); 
	    return req.getResultList(); 
    } 
    
    @Override 
    public List<Employe> consulterEmployesParGroupe(Long idG) 
    { 
//	    Query req = em.createQuery("select e from Employe e where e.groupes.numGroupe =:idg");
	    Query req = em.createQuery("select e from Employe e join e.groupes g where g.numGroupe =:idg"); 
	    req.setParameter("idg", idG);
	    return  req.getResultList();
    } 
    
    @Override 
    public Employe consulterEmploye(Long idEmp) 
    { 
	    Employe e = em.find(Employe.class,idEmp); 
	    if(e==null) 
	    	throw new RuntimeException("Employe "+idEmp+" n'existe pas"); 
	    return e; 
    }

	@Override
	public List<Operation> consulterOperations() 
	{
		Query req = em.createQuery("select o from Operation o"); 
	    return req.getResultList();
	}

	@Override
	public List<Operation> consulterOperationsParEmploye(Long idEmp) 
	{
		Query req = em.createQuery("select o from Operation o where o.employe.numEmploye=:num"); 
	    req.setParameter("num", idEmp); 
	    return req.getResultList();
	}
	
	@Override
	public List<Operation> consulterOperationsParCompte(String numCmp)
	{
		Query req = em.createQuery("select o from Operation o where o.compte.numCompte like :num"); 
	    req.setParameter("num", numCmp); 
	    return req.getResultList();
	}

	@Override
	public List<Compte> consulterComptes() {
		Query req = em.createQuery("select c from Compte c"); 
	    return req.getResultList();
	}

	@Override
	public Client consulterClient(Long codeClient) 
	{
	    Client cl = em.find(Client.class,codeClient); 
	    if(cl==null) 
	    	throw new RuntimeException("Client "+codeClient+" n'existe pas!"); 
	    Query req = em.createQuery("select c from Compte c where c.client.codeClient=:code"); 
	    req.setParameter("code", codeClient); 
	    cl.setComptes(req.getResultList());
	    return cl;
	}

	@Override
	public void supprimerClient(Long codeClient) 
	{
		Client cl = em.find(Client.class, codeClient);
		em.remove(cl);
	}

	@Override
	public List<Employe> consulterEmployesArbre(Long numEmploye) 
	{
		Query req = em.createQuery("select e from Employe e where e.supHierarchique.numEmploye =:num"); 
	    req.setParameter("num", numEmploye); 
	    return req.getResultList();
	}

	@Override
	public List<Groupe> consulterGroupesParEmpolye(Long numEmp)
	{
		Query req = em.createQuery("select g from Groupe g join g.employes e where e.numEmploye=:num"); 
	    req.setParameter("num", numEmp);
	    return req.getResultList();
	}

	@Override
	public List<Compte> consulterComptesParEmpolye(Long numEmp) 
	{
		Query req = em.createQuery("select c from Compte c where c.employe.numEmploye =:num"); 
//		Query req = em.createQuery("select c from Compte c join c.employe e where e.numEmploye=:num"); 
	    req.setParameter("num", numEmp);
	    return req.getResultList();
	}

}
