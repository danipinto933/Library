package com.DaniCRUD.fullStackBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.FileData;
import java.util.Optional;

@Repository
public interface FileDataRepository extends JpaRepository<FileData, Long>
{
    Optional<FileData> findByName(String fileName);
}
