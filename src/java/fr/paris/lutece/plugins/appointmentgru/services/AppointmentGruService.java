/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.identitystore.v2.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v2.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * The Class AppointmentGruService.
 */
public class AppointmentGruService
{
    // Properties
    private static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER = "appointmentgru.attribute.user.home-info.telecom.telephone.number";
    private static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER = "appointmentgru.attribute.user.home-info.telecom.mobile.number";
    private static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER = AppPropertiesService
            .getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER );
    private static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER = AppPropertiesService
            .getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER );

    // Beans
    public static final String BEAN_NAME = "appointmentgru.appointmentGruService";

    /** Instance of the service. */
    private static volatile AppointmentGruService _instance;

    /**
     * Singleton AppointmentGruService Get an instance of the service.
     *
     * @return An instance of the service
     */
    public static AppointmentGruService getService( )
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
     * @param appointment
     *            the appointment
     * @param strKey
     *            the str key
     * @return the appointment gru
     */
    public AppointmentGru getAppointmentGru( Appointment appointment, String strKey )
    {
        AppointmentGru appointmentGru = new AppointmentGru( appointment );
        if ( AppLogService.isDebugEnabled( ) )
        {
            AppLogService.debug( "AppointmentGru  : GUID from appointment Cuid: " + appointment.getGuid() );
        }

        IdentityDto identityDto = buildIdentity( appointment, strKey );

        // call provisioning
        if ( identityDto != null )
        {
            appointmentGru.setGuid( identityDto.getConnectionId( ) );
            appointmentGru.setCuid( identityDto.getCustomerId( ) );

            AttributeDto attributeMobilePhone = identityDto.getAttributes( ).get( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER );

            if ( attributeMobilePhone != null )
            {
                appointmentGru.setMobilePhoneNumber( attributeMobilePhone.getValue( ) );
            }
        }
        appointmentGru.setDemandeTypeId( getDemandeTypeId( appointment, strKey ) );

        return appointmentGru;
    }

    /**
     * Builds an identity from appointment.
     *
     * @param appointment
     *            the appointment
     * @param strKey
     *            the key
     * @return the identity
     */
    private IdentityDto buildIdentity( Appointment appointment, String strKey )
    {
        IdentityDto identityDto = null;
        Map<String, AttributeDto> mapAttributes = new HashMap<>( );

        if ( appointment != null )
        {
            identityDto = new IdentityDto( );

            identityDto.setConnectionId( appointment.getGuid( ) );
            mapAttributes.put( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER,
                    buildAttribute( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER, getMobilePhoneNumber( appointment, strKey ) ) );
            mapAttributes.put( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER,
                    buildAttribute( ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER, getFixedPhoneNumber( appointment, strKey ) ) );
            identityDto.setAttributes( mapAttributes );
        }

        return identityDto;
    }

    /**
     * build an attributeDTO
     * 
     * @param strCode
     *            the attribute code
     * @param strValue
     *            the attribute value
     */
    private static AttributeDto buildAttribute( String strCode, String strValue )
    {
        AttributeDto attributeDto = new AttributeDto( );
        attributeDto.setKey( strCode );
        attributeDto.setValue( strValue );

        return attributeDto;
    }

    /**
     * Gets the mobile phone number.
     *
     * @param appointment
     *            the appointment
     * @param strKey
     *            the str key
     * @return the mobile phone number
     */
    private String getMobilePhoneNumber( Appointment appointment, String strKey )
    {
        NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strKey );
        String strPhoneNumber = StringUtils.EMPTY;
        List<Response> listResponses = AppointmentResponseService.findListResponse( appointment.getIdAppointment( ) );

        if ( mapping != null )
        {
            for ( Response response : listResponses )
            {
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );

                if ( entry.getPosition( ) == mapping.getMobilePhoneNumber( ) )
                {
                    strPhoneNumber = response.getResponseValue( );
                }
            }
        }

        return strPhoneNumber;
    }

    /**
     * Gets the fixed phone number.
     *
     * @param appointment
     *            the appointment
     * @param strKey
     *            the str key
     * @return the fixed phone number
     */
    private String getFixedPhoneNumber( Appointment appointment, String strKey )
    {
        NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strKey );
        String strPhoneNumber = StringUtils.EMPTY;
        List<Response> listResponses = AppointmentResponseService.findListResponse( appointment.getIdAppointment( ) );

        if ( mapping != null )
        {
            for ( Response response : listResponses )
            {
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );

                if ( entry.getPosition( ) == mapping.getFixedPhoneNumber( ) )
                {
                    strPhoneNumber = response.getResponseValue( );
                }
            }
        }

        return strPhoneNumber;
    }

    private int getDemandeTypeId( Appointment appointment, String strKey )
    {
        NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strKey );

        if ( mapping != null )
        {
            return mapping.getDemandeTypeId( );
        }
        else
        {
            return 0;
        }
    }
}
