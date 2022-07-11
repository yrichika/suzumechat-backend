package com.example.suzumechat.service.guest;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@Table(name="guest")
public class Guest {
    // もともと、AuthenticatedClientsとClientLoginRequestsに分かれていたフィールドを１つにまとめた。
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = true)
    private String guestIdHashed; // AuthenticatedClientIdHashed
    
    @Column(nullable = true)
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] guestIdEnc; // AuthenticatedClientIdEnc

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
    @Column(unique = true)
    private String visitorIdHashed; // ClientLoginRequests.requestClientIdHash
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] visitorIdEnc; // ClientLoginRequests.requestClientIdEnc
    
    private String channelId;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
}
