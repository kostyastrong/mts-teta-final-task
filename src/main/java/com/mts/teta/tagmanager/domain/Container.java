package com.mts.teta.tagmanager.domain;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "container")
@Getter
@Setter(PACKAGE)
@NoArgsConstructor(access = PROTECTED)
public class Container {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Size(message = "Max container name size is {max} but value is ${validatedValue}", max = 200)
  @NotNull
  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "app_id")
  @NotNull(message = "App field is null but container should always reference some App")
  private App app;

  @OneToMany(fetch = LAZY, mappedBy = "trigger")
  private List<Trigger> triggers = new ArrayList<>();

  public static Container newContainer(String name, App app) {
    final var container = new Container();
    container.setName(name);
    container.setApp(app);
    return container;
  }
}
