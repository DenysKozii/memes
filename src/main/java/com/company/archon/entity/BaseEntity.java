package com.company.archon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity extends AutoUpdatable {

}