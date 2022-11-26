package com.example.suzumechat.service.guest;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.suzumechat.service.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "guest")
public class Guest implements Serializable {
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

    // DELETE: not necessary
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] codenameEnc; // ClientLoginRequests.codenameEnc

    // DELETE: not necessary
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] passphraseEnc; // ClientLoginRequests.passphraseEnc

    // true: authenticated
    // false: rejected
    // null: not authenticated nor rejected yet
    // Not using Optional because this is using null as one of the states
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "channelId", referencedColumnName = "channelId",
        insertable = false, updatable = false)
    private Channel channel;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
}
