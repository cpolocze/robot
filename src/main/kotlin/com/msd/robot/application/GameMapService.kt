package com.msd.robot.application

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.netty.channel.ChannelOption
import io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class GameMapService {

    private val gameMapClient: WebClient

    object GameMapServiceMetaData {
        const val GAME_MAP_SERVICE_URL = "http://localhost:8080"
        const val NEIGHBOR_CHECK_URI = "/getNeighbor"
        const val NEIGHBOR_CHECK_START_PLANET_PARAM = "startPlanet"
        const val NEIGHBOR_CHECK_TARGET_PLANET_PARAM = "targetPlanet"
    }

    init {
        val httpClient: HttpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            }

        gameMapClient = WebClient.builder()
            .baseUrl(GameMapServiceMetaData.GAME_MAP_SERVICE_URL)
            .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

    fun retrieveTargetPlanetIfRobotCanReach(startPlanetID: UUID, targetPlanetID: UUID): GameMapPlanetDto {
        val uriSpec = gameMapClient.get()
        val querySpec = uriSpec.uri {
            it.path(GameMapServiceMetaData.NEIGHBOR_CHECK_URI)
                .queryParam(GameMapServiceMetaData.NEIGHBOR_CHECK_START_PLANET_PARAM, startPlanetID.toString())
                .queryParam(GameMapServiceMetaData.NEIGHBOR_CHECK_TARGET_PLANET_PARAM, targetPlanetID.toString())
                .build()
        }
        val response = querySpec.exchangeToMono { response ->
            if (response.statusCode() == HttpStatus.OK)
                response.bodyToMono(String::class.java)
            else if (response.statusCode().is4xxClientError)
                throw InvalidMoveException("The robot cannot move to the planet with ID $targetPlanetID")
            else
                throw ClientInternalException(
                    "GameMap Client returned internal error when retrieving targetPlanet " +
                        "for movement"
                )
        }.block()!!

        return jacksonObjectMapper().readValue(response)
    }
}
