package com.polaris.app.passenger.repository.pg

import com.polaris.app.passenger.repository.ShuttleRepository
import com.polaris.app.passenger.repository.StatusType
import com.polaris.app.passenger.repository.entity.ShuttleEntity
import com.polaris.app.passenger.repository.entity.StopEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class ShuttlePgRepository(val db: JdbcTemplate): ShuttleRepository{
    override fun findShuttles(serviceID: Int): List<ShuttleEntity> {
        val shuttleEntities = db.query(
                "SELECT * FROM shuttle INNER JOIN shuttle_activity ON (shuttle.\"ID\" = shuttle_activity.shuttleid) INNER JOIN assignment ON (shuttle_activity.assignmentid = assignment.assignmentid) WHERE shuttle.serviceid = ? AND shuttle_activity.assignmentid != null;",
                arrayOf(serviceID),{
                    resultSet, rowNum -> ShuttleEntity(
                        resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("iconcolor"),
                        resultSet.getInt("assignmentid"),
                        resultSet.getString("routename"),
                        status = StatusType.valueOf(resultSet.getString("status"))
                    )
                }
        )
        return shuttleEntities
    }

    override fun findStops(assignmentID: Int): List<StopEntity> {
        val stopEntities = db.query(
                "SELECT * FROM assignment_stop WHERE assignmentid = ?;",
                arrayOf(assignmentID),{
                    resultSet, rowNum -> StopEntity(
                        resultSet.getInt("assignment_stop_id"),
                        resultSet.getInt("assignmentid"),
                        resultSet.getInt("Index"),
                        resultSet.getTimestamp("estimatedtimeofarrival").toLocalDateTime(),
                        resultSet.getTimestamp("estimatedtimeofdeparture").toLocalDateTime(),
                        resultSet.getTimestamp("timeofarrival").toLocalDateTime(),
                        resultSet.getTimestamp("timeofdeparture").toLocalDateTime(),
                        resultSet.getString("address"),
                        resultSet.getBigDecimal("latitude"),
                        resultSet.getBigDecimal("longitude")
                    )
                }
        )
        return stopEntities
    }
}