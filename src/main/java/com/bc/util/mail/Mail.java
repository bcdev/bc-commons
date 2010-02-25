/*
 * $Id: Mail.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.util.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.logging.Logger;

public class Mail {

    public static boolean sendMessage(Session session, InternetAddress from, String to, String subject,
                                      String text) {
        boolean sendOK = true;
        try {
            InternetAddress[] tos = getAddressArray(to);
            MimeMessage message = new MimeMessage(session);
            message.addRecipients(Message.RecipientType.TO, tos);

            message.setFrom(from);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            Logger.getLogger("com.bc.util").severe("Mail messages NOT send: '" + e.getMessage() + "'");
            sendOK = false;
        }
        return sendOK;
    }

    public static Session getSession(String mailServer) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailServer);
        return Session.getInstance(properties, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static InternetAddress[] getAddressArray(String internetAddress) {

        final StringTokenizer st = new StringTokenizer(internetAddress, ";", false);
        final List addressList = new ArrayList();

        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            addressList.add(token);
        }
        InternetAddress[] addresses = new InternetAddress[addressList.size()];
        for (int i = 0; i < addressList.size(); i++) {
            try {
                addresses[i] = new InternetAddress((String) addressList.get(i));
            } catch (AddressException e) {
                Logger.getLogger("com.bc.util").severe("Failed to build e-mail adress: '" + e.getMessage() + "'");
            }
        }
        return addresses;
    }
}
