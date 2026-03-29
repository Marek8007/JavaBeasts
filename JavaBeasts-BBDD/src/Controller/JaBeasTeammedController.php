<?php

declare(strict_types=1);

namespace App\Controller;

use App\Entity\Jabeas;
use App\Entity\JabeasTeammed;
use App\Entity\Moves;
use App\Entity\Teams;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class JaBeasTeammedController extends AbstractController
{

    /**
     * @Route("/ja-beas-teammed")
     */
    public function index(): Response
    {
        return $this->render('ja_beas_teammed/index.html.twig');
    }

    #Función auxiliar para insertar un JaBeas a un equipo
    public function persistJaBeasTeammed(int $jabeasId, int $move1Id, int $move2Id, Teams $team, int $slot) {
        $entityManager = $this->getDoctrine()->getManager();

        $jabeas = $this->getDoctrine()
            ->getRepository(Jabeas::class)
            ->findOneBy(["jabeasId"=>$jabeasId]);

        $move1 = $this->getDoctrine()
            ->getRepository(Moves::class)
            ->findOneBy(["moveId"=>$move1Id]);

        $move2 = $this->getDoctrine()
            ->getRepository(Moves::class)
            ->findOneBy(["moveId"=>$move2Id]);

        $jabeas_teammed = new JabeasTeammed();

        $jabeas_teammed->setTeam($team);
        $jabeas_teammed->setJabeas($jabeas);
        $jabeas_teammed->setMove1($move1);
        $jabeas_teammed->setMove2($move2);
        $jabeas_teammed->setSlot($slot);

        $entityManager->persist($jabeas_teammed);
    }

}
