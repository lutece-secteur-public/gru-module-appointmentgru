/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.appointmentgru.business;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;

/**
 * This is the business class for the object AppointmentGru.
 */
public class AppointmentGru
{
    /** The _str guid. */
    // Variables declarations
    private String _strGuid;

    /** The _str cuid. */
    private String _nCuid;

    /** The _appointment. */
    private Appointment _appointment;

    /** The _str mobile phone number. */
    private String _strMobilePhoneNumber;

    /** The _str fixed phone number. */
    private String _strFixedPhoneNumber;

    /** The _n type of the demand */
    private int _nDemandeTypeId;

    /**
     * Instantiates a new appointment gru.
     *
     * @param appointment
     *            the appointment
     */
    public AppointmentGru( Appointment appointment )
    {
        setAppointment( appointment );
    }

    /**
     * Gets the fixed phone number.
     *
     * @return the _strFixedPhoneNumber
     */
    public String getFixedPhoneNumber( )
    {
        return _strFixedPhoneNumber;
    }

    /**
     * Sets the fixed phone number.
     *
     * @param fixedPhoneNumber
     *            the new fixed phone number
     */
    public void setFixedPhoneNumber( String fixedPhoneNumber )
    {
        this._strFixedPhoneNumber = fixedPhoneNumber;
    }

    /**
     * Gets the appointment.
     *
     * @return the _appointment
     */
    public Appointment getAppointment( )
    {
        return _appointment;
    }

    /**
     * Sets the appointment.
     *
     * @param appointment
     *            the new appointment
     */
    public void setAppointment( Appointment appointment )
    {
        this._appointment = appointment;
    }

    /**
     * Returns the _strMobilePhoneNumber.
     *
     * @return The _strMobilePhoneNumber
     */
    public String getMobilePhoneNumber( )
    {
        return _strMobilePhoneNumber;
    }

    /**
     * Sets the mobilePhoneNumber.
     *
     * @param mobilePhoneNumber
     *            The mobilePhoneNumber
     */
    public void setMobilePhoneNumber( String mobilePhoneNumber )
    {
        _strMobilePhoneNumber = mobilePhoneNumber;
    }

    /**
     * Returns the Guid.
     *
     * @return The Guid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * Sets the Guid.
     *
     * @param strGuid
     *            The Guid
     */
    public void setGuid( String strGuid )
    {
        _strGuid = strGuid;
    }

    /**
     * Returns the Cuid.
     *
     * @return The Cuid
     */
    public String getCuid( )
    {
        return _nCuid;
    }

    /**
     * Sets the Cuid.
     *
     * @param nCuid
     *            The Cuid
     */
    public void setCuid( String nCuid )
    {
        _nCuid = nCuid;
    }

    /**
     * @return the DemandeTypeId
     */
    public int getDemandeTypeId( )
    {
        return _nDemandeTypeId;
    }

    /**
     * @param nDemandeTypeId
     *            the DemandeTypeId to set
     */
    public void setDemandeTypeId( int nDemandeTypeId )
    {
        this._nDemandeTypeId = nDemandeTypeId;
    }

}
