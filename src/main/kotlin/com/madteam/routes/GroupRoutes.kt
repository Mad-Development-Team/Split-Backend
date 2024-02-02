package com.madteam.routes

import com.madteam.data.model.Group
import com.madteam.data.model.Member
import com.madteam.data.request.CreateNewGroupRequest
import com.madteam.data.response.CreateNewGroupResponse
import com.madteam.generateInviteCode
import com.madteam.getCurrentDateTime
import com.madteam.getRandomColorInHex
import com.madteam.repository.GroupRepository
import com.madteam.repository.MemberRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                groupRepository.createGroup(
                    Group(
                        groupName = request.groupName,
                        groupDescription = request.groupDescription,
                        inviteCode = inviteCode,
                        image = null,
                        bannerImage = null,
                        createdDate = getCurrentDateTime(),
                        currency = request.currency
                    )
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating group")
                return@post
            }

            try {
                request.membersList.forEach { member ->
                    val color = member.color ?: getRandomColorInHex()
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
                inviteCode = newGroup.inviteCode,
                image = newGroup.image,
                bannerImage = newGroup.bannerImage,
                currency = newGroup.currency
            )

            call.respond(HttpStatusCode.OK, response)
        }
    }
}

fun Route.getUserGroups() {
    val groupRepository = GroupRepository()
    val memberRepository = MemberRepository()

    authenticate {
        get("getUserGroups") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)?.toIntOrNull()

            if (userId != null) {
                val groups = groupRepository.getUserGroups(userId)

                val response = groups.map { group ->
                    val members =
                        memberRepository.getGroupMembers(group.id ?: throw IllegalStateException("Group ID is null"))
                    Group(
                        id = group.id,
                        groupName = group.groupName,
                        groupDescription = group.groupDescription,
                        createdDate = group.createdDate,
                        inviteCode = group.inviteCode,
                        image = group.image,
                        bannerImage = group.bannerImage,
                        members = members,
                        currency = group.currency
                    )
                }
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            }
        }
    }
}
