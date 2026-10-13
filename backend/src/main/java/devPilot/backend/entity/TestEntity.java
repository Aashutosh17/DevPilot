package devPilot.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "connection_test")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
}