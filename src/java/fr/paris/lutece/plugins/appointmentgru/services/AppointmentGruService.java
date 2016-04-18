/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointmentgru.services;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.customerprovisioning.business.UserDTO;
import fr.paris.lutece.plugins.customerprovisioning.services.ProvisioningService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.gru.business.customer.Customer;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;


import org.apache.commons.lang.StringUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class AppointmentGruService.
 */
public class AppointmentGruService
{
    
    /** The Constant BEAN_NAME. */
    public static final String BEAN_NAME = "appointmentgru.appointmentGruService";
    
    /** The Constant POSITION_PHONE_NUMBER. */
    private static final String POSITION_PHONE_NUMBER = "notifygru-appointmentgru.config.provider.PositionUserPhoneNumber";

    /** Instance of the service. */
    private static volatile AppointmentGruService _instance;

    /**
     *  Singleton  AppointmentGruService
     * Get an instance of the service.
     *
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

    
    /**
     * Gets the appointment gru.
     *
     * @param appointment the appointment
     * @param strKey the str key
     * @return the appointment gru
     */
    public AppointmentGru getAppointmentGru( Appointment appointment, String strKey )
    {
        AppointmentGru appointmentGru = new AppointmentGru( appointment );
        String strGuid = null;
        String strCuid = null;
        //hack for appointment when they make guid = admin admin
        AppLogService.info( "AppointmentGru DEBUT  : appointment.getIdUser() : " + appointment.getIdUser(  ) );

        if ( StringUtils.isNumeric( appointment.getIdUser(  ) ) )
        {
            strCuid = appointment.getIdUser(  );
            AppLogService.info( "AppointmentGru TEST  : strCuid OK " + appointment.getIdUser(  ) );
        }
        else
        {
            strGuid = appointment.getIdUser(  );
            AppLogService.info( "AppointmentGru TEST  : strGuid OK " + appointment.getIdUser(  ) );
        }

        AppLogService.info( "AppointmentGru  : GUID from appointment Guid: " + strGuid );
        AppLogService.info( "AppointmentGru  : GUID from appointment Cuid: " + strCuid );

        Customer gruCustomer = ProvisioningService.processGuidCuid( strGuid, strCuid,
                buildUserFromAppointment( appointment, strKey ) );

        //call provisioning
        if ( gruCustomer != null )
        {
            AppLogService.info( "\n\n\n------------------ AppointmentGru  -----------------------------" );
            AppLogService.info( "AppointmentGru  : gruCustomer.getAccountGuid() : " + gruCustomer.getAccountGuid(  ) );
            AppLogService.info( "AppointmentGru  : gruCustomer.getId() : " + gruCustomer.getId(  ) );

            appointmentGru.setGuid( gruCustomer.getAccountGuid(  ) );
            appointmentGru.setCuid( gruCustomer.getId(  ) );
            appointmentGru.setMobilePhoneNumber( gruCustomer.getMobilePhone(  ) );
        }

        return appointmentGru;
    }

  
    /**
     * Builds the user from appointment.
     *
     * @param appointment the appointment
     * @param strKey the str key
     * @return the user dto
     */
    private UserDTO buildUserFromAppointment( Appointment appointment, String strKey )
    {
        UserDTO user = null;

        if ( appointment != null )
        {
            user = new UserDTO(  );
            user.setFirstname( appointment.getFirstName(  ) );
            user.setLastname( appointment.getLastName(  ) );
            user.setEmail( appointment.getEmail(  ) );
            user.setUid( appointment.getIdUser(  ) );
            user.setTelephoneNumber( getMobilePhoneNumber( appointment, strKey ) );
            user.setFixedPhoneNumber( getFixedPhoneNumber( appointment, strKey ) );
        }

        return user;
    }
    

    

    /**
     * Gets the mobile phone number.
     *
     * @param appointment the appointment
     * @param strKey the str key
     * @return the mobile phone number
     */
    private String getMobilePhoneNumber( Appointment appointment, String strKey )
    {
    	
    	NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strKey );
    	String strPhoneNumber = "";
    	 List<Response> listResponses = AppointmentHome.findListResponse( appointment.getIdAppointment(  ) );

    	 if( mapping != null )
    	 {
    		  for ( Response response : listResponses )
    	         { 
    	             Entry entry = EntryHome.findByPrimaryKey( response.getEntry(  ).getIdEntry(  ) );
    	             
    	             if( entry.getPosition(  ) == mapping.getMobilePhoneNumber() )
    	             {
    	            	 strPhoneNumber = response.getResponseValue(  );
    	             }
    	           
    	         }    		 
    	 }       
         
         return strPhoneNumber;
    }
    
    /**
     * Gets the fixed phone number.
     *
     * @param appointment the appointment
     * @param strKey the str key
     * @return the fixed phone number
     */
    private String getFixedPhoneNumber( Appointment appointment, String strKey )
    {    	
    	NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strKey );
    	String strPhoneNumber = "";
    	 List<Response> listResponses = AppointmentHome.findListResponse( appointment.getIdAppointment(  ) );
    	 if( mapping != null )
    	 {
    		  for ( Response response : listResponses )
    	         { 
    	             Entry entry = EntryHome.findByPrimaryKey( response.getEntry(  ).getIdEntry(  ) );
    	             
    	             if( entry.getPosition(  ) == mapping.getMobilePhoneNumber() )
    	             {
    	            	 strPhoneNumber = response.getResponseValue(  );
    	             }    	           
    	         }    		 
    	 }         
         return strPhoneNumber;
    }
}
