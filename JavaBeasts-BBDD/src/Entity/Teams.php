<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

/**
 * Teams
 *
 * @ORM\Table(name="Teams", indexes={@ORM\Index(name="fk_teams_user", columns={"User_Id"})})
 * @ORM\Entity
 */
class Teams
{
    /**
     * @var int
     *
     * @ORM\Column(name="Team_Id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     * @Groups({"teams:read"})
     */
    private $teamId;

    /**
     * @var Users
     *
     * @ORM\ManyToOne(targetEntity="Users")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="User_Id", referencedColumnName="User_Id")
     * })
     * @Groups({"teams:read"})
     */
    private $user;

    public function getTeamId(): int
    {
        return $this->teamId;
    }

    public function getUser(): Users
    {
        return $this->user;
    }

    public function setUser(Users $user): void
    {
        $this->user = $user;
    }



}
