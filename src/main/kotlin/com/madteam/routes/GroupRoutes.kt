package com.madteam.routes

import com.madteam.data.model.Group
import com.madteam.data.model.Member
import com.madteam.data.request.CreateNewGroupRequest
import com.madteam.data.request.UpdateUserInfoRequest
import com.madteam.data.response.CreateNewGroupResponse
import com.madteam.data.table.GroupTable
import com.madteam.generateInviteCode
import com.madteam.getCurrentDateTime
import com.madteam.getRandomHexColor
import com.madteam.repository.GroupRepository
import com.madteam.repository.MemberRepository
import com.madteam.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.createNewGroup() {
    val groupRepository = GroupRepository()
    val memberRepository = MemberRepository()

    authenticate {
        post("createNewGroup") {
            val request: CreateNewGroupRequest = try {
                call.receive()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "${e.message} ${e.localizedMessage}")
                return@post
            }

            var inviteCode: String
            do {
                inviteCode = generateInviteCode()
            } while (!groupRepository.isInviteCodeUnique(inviteCode))

            val newGroup: Group = try {
                groupRepository.createGroup(Group(
                    groupName = request.groupName,
                    groupDescription = request.groupDescription,
                    inviteCode = inviteCode,
                    image = null,
                    bannerImage = null,
                    createdDate = getCurrentDateTime()
                ))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating group")
                return@post
            }

            try {
                request.membersList.forEach { member ->
                    val color = member.color ?: getRandomHexColor()
                    val newMember = Member(
                        name = member.name,
                        profileImage = member.profileImage,
                        user = if (member.user != 0) member.user else null,
                        color = color,
                        joinedDate = getCurrentDateTime(),
                        groupId = newGroup.id ?: throw IllegalStateException("Group ID is null")
                    )
                    memberRepository.createMember(newMember)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating members")
                return@post
            }

            val response = CreateNewGroupResponse(
                id = newGroup.id ?: throw IllegalStateException("Group ID is null"),
                groupName = newGroup.groupName,
                groupDescription = newGroup.groupDescription,
                createdDate = newGroup.createdDate,
                inviteCode = newGroup.inviteCode!!,
                image = newGroup.image,
                bannerImage = newGroup.bannerImage
            )

            call.respond(HttpStatusCode.OK, response)
        }
    }
}
