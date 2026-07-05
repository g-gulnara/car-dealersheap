package ru.gareeva.system.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.gareeva.system.domain.exceptions.EntityNotFoundException;
import ru.gareeva.system.domain.exceptions.StorageUnavailableException;

import javax.naming.ServiceUnavailableException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CarGrpcClient {

    @GrpcClient("storage-service")
    private CarServiceGrpc.CarServiceBlockingStub carServiceStub;

    public List<CarResponse> getAvailableCars() {
        try {
            CarListResponse response = carServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getAvailableCars(Empty.getDefaultInstance());
            log.info("Got {} cars from StorageService", response.getCarsCount());
            return response.getCarsList();
        } catch (StatusRuntimeException e) {

            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new StorageUnavailableException("StorageService timeout");
            }

            log.error("gRPC call failed: {}", e.getStatus());

            throw new StorageUnavailableException("StorageService unavailable");
        }
    }

    public CarResponse getCarById(String id) {
        try {
            return carServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getCarById(CarIdRequest.newBuilder().setId(id).build());
        } catch (StatusRuntimeException e) {

            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new EntityNotFoundException("Car not found: " + id);
            }

            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new StorageUnavailableException("StorageService timeout");
            }

            log.error("gRPC call failed: {}", e.getStatus());

            throw new StorageUnavailableException("StorageService unavailable");
        }
    }
}