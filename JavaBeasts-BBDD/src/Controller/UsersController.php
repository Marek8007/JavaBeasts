<?php

declare(strict_types=1);

namespace App\Controller;

use App\Entity\Teams;
use App\Entity\Users;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Serializer\SerializerInterface;

class UsersController extends AbstractController
{
    #POST USER
    public function users(
        SerializerInterface $serializer,
        Request $request,
        JaBeasTeammedController $JTC
    ): Response {
        $entityManager = $this->getDoctrine()->getManager();

        $decodedUserData = json_decode($request->getContent(), true);
        $username = trim((string) ($decodedUserData['username']));
        $password = (string) ($decodedUserData['password']);

        if ($username === '' || $password === '') {
            return $this->json(
                ['message' => 'Username and password are required'],
                Response::HTTP_BAD_REQUEST
            );
        }

        $existingUser = $this->getDoctrine()
            ->getRepository(Users::class)
            ->findOneBy(['username' => $username]);

        if ($existingUser) {
            return $this->json(
                ['message' => 'That username already exists'],
                Response::HTTP_CONFLICT
            );
        }

        $passwordHash = password_hash($password, PASSWORD_ARGON2I);

//        if ($passwordHash === false) {
//            return $this->json(
//                ['message' => 'Password hash generation failed'],
//                Response::HTTP_INTERNAL_SERVER_ERROR
//            );
//        }

        $newUser = new Users();
        $newUser->setUsername($username);
        $newUser->setPassword($passwordHash);

        $entityManager->persist($newUser);
        $entityManager->flush();

        $team = new Teams();
        $team->setUser($newUser);
        $entityManager->persist($team);
        $entityManager->flush();

        $JTC->persistJaBeasTeammed(1, 1, 2, $team, 1);
        $JTC->persistJaBeasTeammed(5, 10, 11, $team, 2);
        $JTC->persistJaBeasTeammed(9, 19, 20, $team, 3);
        $JTC->persistJaBeasTeammed(13, 28, 29, $team, 4);
        $entityManager->flush();

        $data = $serializer->serialize($newUser, 'json', [
            'groups' => ['users:read'],
        ]);

        return new Response(
            $data,
            Response::HTTP_CREATED,
            ['Content-Type' => 'application/json']
        );
    }

    public function user(Request $request, SerializerInterface $serializer): Response
    {
        if ($request->isMethod('DELETE')) {
            return $this->deleteUser($request);
        }

        if ($request->isMethod('POST')) {
            return $this->login($request, $serializer);
        }

        return $this->json(
            ['message' => 'Method not allowed'],
            Response::HTTP_METHOD_NOT_ALLOWED
        );
    }

    public function login(Request $request, SerializerInterface $serializer): Response
    {
        $data = json_decode($request->getContent(), true);
        $username = trim((string) ($data['username']));
        $password = (string) ($data['password']);

        if ($username === '' || $password === '') {
            return new Response("Username o password no proporcionados", Response::HTTP_BAD_REQUEST, ["Content-Type"=>"application/json"]);
        }

        $user = $this->getDoctrine()
            ->getRepository(Users::class)
            ->findOneBy(['username' => $username]);

        if (!$user || !password_verify($password, $user->getPassword())) {
            return new Response("Credenciales incorrectas", Response::HTTP_UNAUTHORIZED, ["Content-Type"=>"application/json"]);
        }

        $userData = $serializer->serialize($user, 'json', [
            'groups' => ['users:read'],
        ]);

        return new Response($userData, Response::HTTP_OK, ["Content-Type"=>"application/json"]);
    }

    private function deleteUser(Request $request): Response
    {
        $id = $request->get('id');
        $entityManager = $this->getDoctrine()->getManager();

        $user = $entityManager
            ->getRepository(Users::class)
            ->findOneBy(['userId' => $id]);

        if (!$user) {
            return $this->json(
                ['message' => 'User not found'],
                Response::HTTP_NOT_FOUND
            );
        }

        $entityManager->remove($user);
        $entityManager->flush();

        return $this->json(
            ['message' => 'User deleted'],
            Response::HTTP_OK
        );
    }
}
