package com.group4.repository;

import com.group4.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // Tìm kiếm theo tên
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM ProductEntity p JOIN p.detail pd JOIN p.manufacturer m " +
            "WHERE (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:ram IS NULL OR pd.RAM LIKE %:ram%) " +
            "AND (:cpu IS NULL OR pd.CPU LIKE %:cpu%) " +
            "AND (:gpu IS NULL OR pd.GPU LIKE %:gpu%) " +
            "AND (:monitor IS NULL OR pd.monitor LIKE %:monitor%) " +
            "AND (:disk IS NULL OR pd.disk LIKE %:disk%) " +
            "AND (:manufacturerName IS NULL OR m.name LIKE %:manufacturerName%)")
    List<ProductEntity> findByMultipleFilters(@Param("minPrice") Double minPrice,
                                                  @Param("maxPrice") Double maxPrice,
                                              @Param("ram") String ram,
                                              @Param("cpu") String cpu,
                                              @Param("gpu") String gpu,
                                              @Param("monitor") String monitor,
                                              @Param("disk") String disk,
                                              @Param("manufacturerName") String manufacturerName);

    @Query("SELECT p FROM ProductEntity p WHERE "
            + "(:searchName IS NULL OR p.name LIKE %:searchName%) AND "
            + "(:manufacturer IS NULL OR p.manufacturer.name LIKE  %:manufacturer%) AND "
            + "(:cpu IS NULL OR p.detail.CPU LIKE %:cpu%) AND "
            + "(:gpu IS NULL OR p.detail.GPU LIKE %:gpu%) AND "
            + "(:operationSystem IS NULL OR p.detail.operationSystem LIKE %:operationSystem%) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:disk IS NULL OR p.detail.disk LIKE %:disk%) AND "
            + "(:category IS NULL OR p.category.name LIKE %:category%)")
    Page<ProductEntity> findProductsByCriteria(
            @Param("searchName") String searchName,
            @Param("manufacturer") String manufacturer,
            @Param("cpu") String cpu,
            @Param("gpu") String gpu,
            @Param("operationSystem") String operationSystem,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("disk") String disk,
            @Param("category") String category,
            Pageable pageable);

    @Query("SELECT p.name, COUNT(p) FROM ProductEntity p GROUP BY p.name")
    Page<Object[]> countProductsGroupedByName(Pageable pageable);

    // Truy vấn phân trang cho các sản phẩm nhóm theo tên danh mục
    @Query("SELECT p.category.name, COUNT(p) FROM ProductEntity p GROUP BY p.category.name")
    Page<Object[]> countProductsGroupedByCategoryName(Pageable pageable);

    // Truy vấn phân trang cho các sản phẩm nhóm theo tên nhà sản xuất
    @Query("SELECT p.manufacturer.name, COUNT(p) FROM ProductEntity p GROUP BY p.manufacturer.name")
    Page<Object[]> countProductsGroupedByManufacturerName(Pageable pageable);

}
