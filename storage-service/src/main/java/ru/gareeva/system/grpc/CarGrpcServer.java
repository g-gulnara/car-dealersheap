package ru.gareeva.system.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.gareeva.system.abstractions.repositories.CarInStockRepository;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@GrpcService
public class CarGrpcServer extends CarServiceGrpc.CarServiceImplBase {

    private final CarInStockRepository carInStockRepository;

    public CarGrpcServer(CarInStockRepository carInStockRepository) {
        this.carInStockRepository = carInStockRepository;
    }

    @Override
    public void getAvailableCars(Empty request, StreamObserver<CarListResponse> responseObserver) {
        List<CarInStock> cars = carInStockRepository.findAll();

        List<CarResponse> responses = cars.stream()
                .map(this::toCarResponse)
                .collect(Collectors.toList());

        CarListResponse response = CarListResponse.newBuilder()
                .addAllCars(responses)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCarById(CarIdRequest request, StreamObserver<CarResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        carInStockRepository.findById(id)
                .ifPresentOrElse(car -> {
                    responseObserver.onNext(toCarResponse(car));
                    responseObserver.onCompleted();
                }, () -> responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Car not found with id: " + request.getId())
                                .asRuntimeException()
                ));
    }

    private CarResponse toCarResponse(CarInStock car) {
        return CarResponse.newBuilder()
                .setId(car.getId().toString())
                .setBrand(car.getBrand() != null ? car.getBrand() : "")
                .setModelName(car.getModelName() != null ? car.getModelName() : "")
                .setBodyStyle(car.getCarBody() != null ? car.getCarBody().name() : "")
                .setFuelType(car.getFuelType() != null ? car.getFuelType().name() : "")
                .setTransmission(car.getTransmissionType() != null ? car.getTransmissionType().name() : "")
                .setDriveTrain(car.getDriveTrain() != null ? car.getDriveTrain().name() : "")
                .setEnginePower(car.getEnginePower() != null ? car.getEnginePower() : 0)
                .setEngineCapacity(car.getEngineCapacity() != null ? car.getEngineCapacity() : 0)
                .setPrice(car.getPrice() != null ? car.getPrice().getValue().doubleValue() : 0.0)
                .setIsReserved(car.isReserved())
                .build();
    }
}