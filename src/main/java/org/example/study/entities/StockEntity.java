package org.example.study.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class StockEntity {
    private String id;
    private String isin;
    private String marker;
    private String displayText;
    private LocalDateTime createAt;

    public StockEntity() {
    }

    public StockEntity(String id, String isin, String marker, String displayText, LocalDateTime createAt) {
        this.id = id;
        this.isin = isin;
        this.marker = marker;
        this.displayText = displayText;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockEntity that = (StockEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
