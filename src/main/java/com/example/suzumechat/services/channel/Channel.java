package com.example.suzumechat.services.channel;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.springframework.data.relational.core.mapping.Embedded.Nullable;

@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String channelId;
    private String hostIdHashed; // Host Id

    // for channel management
    private String channelTokenId;
    private String channelNameEnc;
    private String hostChannelTokenHashed;
    private String loginChannelTokenHashed;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] loginChannelTokenEnc;
    private String clientChannelTokenHashed;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] clientChannelTokenEnc;
    
    @Nullable
    private String secretKeyEnc;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
