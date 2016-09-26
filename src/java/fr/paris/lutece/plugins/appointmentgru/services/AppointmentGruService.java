/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AuthorDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityChangeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Class AppointmentGruService.
 */
public class AppointmentGruService
{
    // Properties
    private static final String PROPERTIES_APPLICATION_CODE = "appointmentgru.application.code";
    private static final String PROPERTIES_ATTRIBUTE_USER_NAME_GIVEN = "appointmentgru.attribute.user.name.given";
    private static final String PROPERTIES_ATTRIBUTE_USER_NAME_FAMILLY = "appointmentgru.attribute.user.name.family";
    private static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_ONLINE_EMAIL = "appointmentgru.attribute.user.home-info.online.email";
    private static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER = "appointmentgru.attribute.user.home-info.telecom.telephone.number";
    private static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER = "appointmentgru.attribute.user.home-info.telecom.mobile.number";
    private static final String APPLICATION_CODE = AppPropertiesService.getProperty( PROPERTIES_APPLICATION_CODE );
    private static final String ATTRIBUTE_IDENTITY_NAME_GIVEN = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_NAME_GIVEN );
    private static final String ATTRIBUTE_IDENTITY_NAME_FAMILLY = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_NAME_FAMILLY );
    private static final String ATTRIBUTE_IDENTITY_HOMEINFO_ONLINE_EMAIL = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_ONLINE_EMAIL );
    private static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER );
    private static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER );

    // Beans
    public static final String BEAN_NAME = "appointmentgru.appointmentGruService";
    private static final String BEAN_IDENTITYSTORE_SERVICE = "appointmentgru.identitystore.service";

    // Services
    private static IdentityService _identityService;

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
            _identityService = SpringContextService.getBean( BEAN_IDENTITYSTORE_SERVICE );
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
        if ( StringUtils.isNumeric( appointment.getIdUser(  ) ) )
        {
            strCuid = appointment.getIdUser(  );
        }
        else
        {
            strGuid = appointment.getIdUser(  );
        }

        if ( AppLogService.isDebugEnabled(  ) )
        {
            AppLogService.debug( "AppointmentGru  : GUID from appointment Guid: " + strGuid );
            AppLogService.debug( "AppointmentGru  : GUID from appointment Cuid: " + strCuid );
        }

        IdentityChangeDto identityChangeDto = new IdentityChangeDto(  );
        IdentityDto identityDto = buildIdentity( appointment, strKey );

        identityChangeDto.setIdentity( identityDto );

        AuthorDto authorDto = new AuthorDto(  );
        authorDto.setApplicationCode( APPLICATION_CODE );

        identityChangeDto.setAuthor( authorDto );

        identityDto = _identityService.createIdentity( identityChangeDto, StringUtils.EMPTY );

        //call provisioning
        if ( identityDto != null )
        {
            appointmentGru.setGuid( identityDto.getConnectionId(  ) );
            appointmentGru.setCuid( identityDto.getCustomerId(  ) );

            AttributeDto attributeMobilePhone = identityDto.getAttributes(  )
                                                           .get( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER );

            if ( attributeMobilePhone != null )
            {
                appointmentGru.setMobilePhoneNumber( attributeMobilePhone.getValue(  ) );
            }
        }

        return appointmentGru;
    }

    /**
     * Builds an identity from appointment.
     *
     * @param appointment the appointment
     * @param strKey the key
     * @return the identity
     */
    private IdentityDto buildIdentity( Appointment appointment, String strKey )
    {
        IdentityDto identityDto = null;
        Map<String, AttributeDto> mapAttributes = new HashMap<String, AttributeDto>(  );

        if ( appointment != null )
        {
            identityDto = new IdentityDto(  );
            identityDto.setAttributes( mapAttributes );

            identityDto.setConnectionId( appointment.getIdUser(  ) );

            setAttribute( identityDto, ATTRIBUTE_IDENTITY_NAME_GIVEN, appointment.getFirstName(  ) );
            setAttribute( identityDto, ATTRIBUTE_IDENTITY_NAME_FAMILLY, appointment.getLastName(  ) );
            setAttribute( identityDto, ATTRIBUTE_IDENTITY_HOMEINFO_ONLINE_EMAIL, appointment.getEmail(  ) );
            setAttribute( identityDto, ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER,
                getMobilePhoneNumber( appointment, strKey ) );
            setAttribute( identityDto, ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER,
                getFixedPhoneNumber( appointment, strKey ) );
        }

        return identityDto;
    }

    /**
     * Sets an attribute into the specified identity
     * @param identityDto the identity
     * @param strCode the attribute code
     * @param strValue the attribute value
     */
    private static void setAttribute( IdentityDto identityDto, String strCode, String strValue )
    {
        AttributeDto attributeDto = new AttributeDto(  );
        attributeDto.setKey( strCode );
        attributeDto.setValue( strValue );

        identityDto.getAttributes(  ).put( attributeDto.getKey(  ), attributeDto );
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

        if ( mapping != null )
        {
            for ( Response response : listResponses )
            {
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry(  ).getIdEntry(  ) );

                if ( entry.getPosition(  ) == mapping.getMobilePhoneNumber(  ) )
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

        if ( mapping != null )
        {
            for ( Response response : listResponses )
            {
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry(  ).getIdEntry(  ) );

                if ( entry.getPosition(  ) == mapping.getFixedPhoneNumber(  ) )
                {
                    strPhoneNumber = response.getResponseValue(  );
                }
            }
        }

        return strPhoneNumber;
    }
}
