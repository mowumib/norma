package com.hotelbooking.norma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotelbooking.norma.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    public List<Hotel> findByLocation(String location);
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.name) = LOWER(:name)")
    public Optional<Hotel> findByName(@Param("name") String name);
    
    public Optional<Hotel> findByHotelCode(String hotelCode);
    public boolean existsByNameIgnoreCase(String name);
}
