package com.example.suzumechat.services.guest;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="guest")
public class Guest {
    // もともと、AuthenticatedClientsとClientLoginRequestsに分かれていたフィールドを１つにまとめた。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String guestIdHashed;
    
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] guestIdEnc;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] codenameEnc; // ClientLoginRequests.codenameEnc
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] passphraseEnc; // ClientLoginRequests.passphraseEnc

    // true: authenticated, false: rejected, null: not authenticated nor rejected yet
    @Column(nullable = true)
    private Boolean isAuthenticated; // ClientLoginRequest.isAuthenticated;

    // requestClientIdHash
    // visitor when just requested to join a chat channel
    private String visitorIdHashed; // ClientLoginRequests.requestClientIdHash
    private String visitorIdEnc; // ClientLoginRequests.requestClientIdEnc
    private String channelId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
