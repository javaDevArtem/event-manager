package com.amir.eventmanager.location;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final static Logger log = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    private final LocationDtoMapper dtoMapper;

    public LocationController(LocationService locationService,
                              LocationDtoMapper locationDtoMapper) {
        this.locationService = locationService;
        this.dtoMapper = locationDtoMapper;
    }

    @GetMapping()
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        log.info("Get Request for get all locations");
        List<Location> locationList = locationService.getAllLocation();
        return ResponseEntity.ok(locationList.stream().map(dtoMapper::toDto).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto
    ) {
        log.info("Post Request for location create: locationDto={}", locationDto);
        Location location = locationService.createLocation(dtoMapper.toDomain(locationDto));
        return ResponseEntity.status(201)
                .body(dtoMapper.toDto(location));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<LocationDto> deleteLocations(
            @PathVariable(name = "locationId") Long locationId
    ) {
        log.info("Get Request for delete location: locationId={}", locationId);
        Location deletedLocation = locationService.deleteLocation(locationId);
        return ResponseEntity.status(204).body(dtoMapper.toDto(deletedLocation));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable(name = "locationId") Long locationId
    ) {
        log.info("Get Request for get location: locationId={}", locationId);
        Location locationById = locationService.getLocationById(locationId);
        return ResponseEntity.status(200).body(dtoMapper.toDto(locationById));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable(name = "locationId") Long locationId,
            @RequestBody @Valid LocationDto updateLocationDTO
    ) {
        log.info("Get Request for update location: locationId={}, updateLocationDTO={}", locationId, updateLocationDTO);
        Location updatedLocation = locationService.updateLocation(dtoMapper.toDomain(updateLocationDTO), locationId);
        return ResponseEntity.ok(dtoMapper.toDto(updatedLocation));
    }
}
