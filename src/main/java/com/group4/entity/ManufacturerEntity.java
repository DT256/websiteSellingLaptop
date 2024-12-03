	package com.group4.entity;
	
	import com.fasterxml.jackson.annotation.JsonManagedReference;
	import jakarta.persistence.*;
	import lombok.*;
	
	import java.util.List;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Entity
	@Table(name = "manufacturers")
	public class ManufacturerEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "manufacturer_id")
	    private Long id;
	
	    @Column(name = "name", columnDefinition = "nvarchar(250)", nullable = false)
	    private String name;
	
	    @Column(name = "address", columnDefinition = "nvarchar(250)")
	    private String address;
	
	    @OneToMany(mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = false)
		@JsonManagedReference
	    private List<ProductEntity> products;
	
	}
