
package fr.paris.lutece.plugins.appointmentgru.services;



import fr.paris.lutece.plugins.appointment.business.Appointment;

import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.customerprovisioning.business.UserDTO;

import fr.paris.lutece.plugins.customerprovisioning.services.ProvisioningService;
import fr.paris.lutece.plugins.gru.business.customer.Customer;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public  class AppointmentGruService {

	
	 public static final String BEAN_NAME = "appointmentgru.appointmentGruService";
	 /**
     * Instance of the service
     */
    private static volatile AppointmentGruService _instance;

    /** Singleton  AppointmentGruService
     * Get an instance of the service
     * @return An instance of the service
     */
    public static AppointmentGruService getService(  )
    {
        if ( _instance == null )
        {
            _instance = SpringContextService.getBean( BEAN_NAME );
        }

        return _instance;
    }
	
	public  AppointmentGru getAppointmentGru(Appointment appointment)
	{
	
		AppointmentGru appointmentGru = new AppointmentGru(appointment);	
		String strGuid = appointment.getIdUser();
		String strCuid = null;
		Customer gruCustomer  = ProvisioningService.processGuidCuid( strGuid, strCuid, buildUserFromAppointment(appointment) );
		AppLogService.info("\n\n\n------------------ AppointmentGru  -----------------------------");
		AppLogService.info("AppointmentGru  : gruCustomer.getAccountGuid() : "+gruCustomer.getAccountGuid());
		AppLogService.info("AppointmentGru  : gruCustomer.getId() : "+gruCustomer.getId());
		//call provisioning
		if(gruCustomer!=null)
		{
			appointmentGru.setGuid(gruCustomer.getAccountGuid());
			appointmentGru.setCuid(gruCustomer.getId());
		}		
	
		
		return appointmentGru;
	}
	
	  private  UserDTO buildUserFromAppointment( Appointment appointment )
	    {
	        UserDTO user = null;

	        if ( appointment != null )
	        {
	            user = new UserDTO(  );
	            user.setFirstname( appointment.getFirstName() ); 
	            user.setLastname( appointment.getLastName( ) );
	            user.setEmail( appointment.getEmail() );
	            user.setUid( appointment.getIdUser() );	       
	          
	        }

	        return user;
	    }
}
