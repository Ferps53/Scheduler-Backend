package com.scheduler.core.table.sync.dto;

import java.util.List;

public record TableSyncDTO(String name, List<?> rows) {
}
