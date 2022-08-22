package com.example.suzumechat.service.channel;

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
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String channelId;
    private String hostIdHashed; // Host Id

    // for channel management
    private String channelTokenId;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] channelNameEnc;
    private String hostChannelTokenHashed;

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
    
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
}
