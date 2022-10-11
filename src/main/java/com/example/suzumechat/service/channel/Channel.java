package com.example.suzumechat.service.channel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.suzumechat.service.guest.Guest;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "channel")
public class Channel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // to use this as ad with encryption
    @NaturalId
    @Column(unique = true)
    private String channelId;

    private String hostIdHashed; // Host Id

    // for channel management
    private String channelTokenId;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] channelNameEnc;

    private String hostChannelTokenHashed;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] hostChannelTokenEnc;


    private String joinChannelTokenHashed;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] joinChannelTokenEnc;

    private String guestChannelTokenHashed; // clientChannelTokenHashed
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] guestChannelTokenEnc; // clientChannelTokenEnc

    @Column(nullable = true)
    private byte[] secretKeyEnc;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "channelId", referencedColumnName = "channelId",
            insertable = false, updatable = true)
    private Set<Guest> guests;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;


    public boolean secretKeyEmpty() {
        if (secretKeyEnc == null) {
            return true;
        }
        return false;
    }

    public boolean isClosed() {
        return secretKeyEmpty();
    }
}
