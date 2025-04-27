package com.amir.eventmanager.location;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository repository;

    private final LocationEntityMapper entityMapper;

    public LocationService(LocationRepository locationRepository,
                           LocationEntityMapper entityMapper) {
        this.repository = locationRepository;
        this.entityMapper = entityMapper;
    }

    public List<Location> getAllLocation() {
        return repository.findAll().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Location createLocation(Location locationToCreate) {
        if (locationToCreate.id() != null) {
            throw new IllegalArgumentException("Can not create location with provided id. Id must be empty");
        }
        LocationEntity createdLocation = repository.save(entityMapper.toEntity(locationToCreate));
        return entityMapper.toDomain(createdLocation);
    }

    public Location updateLocation(Location locationToUpdate, Long locationId) {
        if (locationToUpdate.id() != null) {
            throw new IllegalArgumentException("Can not update location with provided id. Id must be empty");
        }
        LocationEntity entityToUpdate = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s".formatted(locationId)));
        entityToUpdate.setAddress(locationToUpdate.address());
        entityToUpdate.setName(locationToUpdate.name());
        entityToUpdate.setCapacity(locationToUpdate.capacity());
        entityToUpdate.setDescription(locationToUpdate.description());
        LocationEntity updatedEntity = repository.save(entityToUpdate);
        return entityMapper.toDomain(updatedEntity);
    }

    public Location deleteLocation(Long locationId) {
        LocationEntity locationToDelete = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s".formatted(locationId)));
        repository.deleteById(locationId);
        return entityMapper.toDomain(locationToDelete);

    }

    public Location getLocationById(Long locationId) {
        LocationEntity foundEntity = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s".formatted(locationId)));
        return entityMapper.toDomain(foundEntity);

    }
}
